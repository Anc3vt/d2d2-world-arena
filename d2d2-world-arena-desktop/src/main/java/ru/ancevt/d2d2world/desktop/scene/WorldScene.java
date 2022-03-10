/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2world.desktop.scene;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.commons.concurrent.Async;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2world.control.LocalPlayerController;
import ru.ancevt.d2d2world.debug.DebugPanel;
import ru.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import ru.ancevt.d2d2world.gameobject.PlayerActor;
import ru.ancevt.d2d2world.map.MapIO;
import ru.ancevt.d2d2world.mapkit.MapkitManager;
import ru.ancevt.d2d2world.net.dto.MapLoadedDto;
import ru.ancevt.d2d2world.sync.SyncDataReceiver;
import ru.ancevt.d2d2world.sync.SyncMotion;
import ru.ancevt.d2d2world.world.World;

import java.io.IOException;

import static ru.ancevt.d2d2world.desktop.DesktopConfig.DEBUG_WORLD_ALPHA;
import static ru.ancevt.d2d2world.desktop.DesktopConfig.MODULE_CONFIG;
import static ru.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static ru.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;

@Slf4j
public class WorldScene extends DisplayObjectContainer {

    private final World world;
    private final LocalPlayerController localPlayerController = new LocalPlayerController();
    private final BitmapText debug;
    //private final ShadowRadial shadowRadial;
    private boolean eventsAdded;

    private long frameCounter;

    public WorldScene() {
        world = new World();
        world.getPlayProcessor().setEnabled(false);

        ((SyncDataReceiver)MODULE_CLIENT.getSyncDataReceiver()).setWorld(world);

        world.getCamera().setBoundsLock(true);
        world.setVisible(false);

        setScale(2f, 2f);

        add(world);

        localPlayerController.setEnabled(true);

        /*shadowRadial = new ShadowRadial() {
            @Override
            public void onEachFrame() {
                setXY(localPlayerActor.getX() + 35, localPlayerActor.getY());
            }
        };
        shadowRadial.setScale(2f,2f);*/
        //world.add(shadowRadial);

        debug = new BitmapText();
        debug.setText("debug");

        world.setAlpha(MODULE_CONFIG.getFloat(DEBUG_WORLD_ALPHA));

        addEventListener(WorldScene.class, Event.ADD_TO_STAGE, this::this_addToStage);
    }

    private void this_addToStage(Event event) {
        removeEventListeners(WorldScene.class);

        getRoot().add(new DebugPanel(SyncMotion.class.getSimpleName()));
    }

    public void init() {
        world.clear();
    }

    public void loadMap(String mapFilename) {
        MODULE_CLIENT.getSyncDataReceiver().setEnabled(false);

        MapkitManager.getInstance().disposeExternalMapkits();

        world.setSceneryPacked(false);
        world.clear();

        Async.run(() -> {
            try {
                long timeBefore = System.currentTimeMillis();
                world.setMap(MapIO.load(mapFilename));
                mapLoaded();
                log.info("Map '" + mapFilename + "' loaded {}ms", (System.currentTimeMillis() - timeBefore));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private void mapLoaded() {
        world.setSceneryPacked(true);

        world.getCamera().setViewportSize(getStage().getStageWidth(), getStage().getStageHeight());

        setXY(getStage().getStageWidth() / 2, getStage().getStageHeight() / 2);

        //getRoot().add(debug, 10, 250);

        addRootAndChatEventsIfNotYet();

        start();

        dispatchEvent(new SceneEvent(SceneEvent.MAP_LOADED, this));

        MODULE_CLIENT.sendExtra(MapLoadedDto.EMPTY);
        MODULE_CLIENT.getSyncDataReceiver().setEnabled(true);
    }

    private void addRootAndChatEventsIfNotYet() {
        if (!eventsAdded) {
            getRoot().addEventListener(InputEvent.KEY_DOWN, event -> {
                var e = (InputEvent) event;
                final int oldState = localPlayerController.getState();
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), true);

                if (oldState != localPlayerController.getState()) {
                    MODULE_CLIENT.sendLocalPlayerController(localPlayerController.getState());
                }
                /*if (e.getKeyCode() == KeyCode.F11) {
                    if(shadowRadial.getDarknessValue() == 0) return;
                    shadowRadial.setDarknessValue(shadowRadial.getDarknessValue() - 1);
                } else if (e.getKeyCode() == KeyCode.F12) {
                    shadowRadial.setDarknessValue(shadowRadial.getDarknessValue() + 1);
                }*/
            });
            getRoot().addEventListener(InputEvent.KEY_UP, event -> {
                var e = (InputEvent) event;
                final int oldState = localPlayerController.getState();
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), false);
                if (oldState != localPlayerController.getState()) {
                    MODULE_CLIENT.sendLocalPlayerController(localPlayerController.getState());
                }
            });

            MODULE_CHAT.addEventListener(ChatEvent.CHAT_INPUT_OPEN, e -> localPlayerController.setEnabled(false));
            MODULE_CHAT.addEventListener(ChatEvent.CHAT_INPUT_CLOSE, e -> localPlayerController.setEnabled(true));
            eventsAdded = true;
        }
    }

    public void setLocalPlayerActorGameObjectId(int playerActorGameObjectId) {
        PlayerActor localPlayerActor = (PlayerActor) world.getGameObjectById(playerActorGameObjectId);
        localPlayerActor.setController(localPlayerController);
        localPlayerActor.setLocalPlayerActor(true);
        world.getCamera().setAttachedTo(localPlayerActor);
        world.getCamera().setBoundsLock(true);
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();
        if (!MODULE_CLIENT.isConnected() || !MODULE_CLIENT.isEnteredServer()) return;

        SyncMotion.process();

        if (frameCounter % 1000 == 0) {
            MODULE_CLIENT.sendServerInfoRequest();
            MODULE_CLIENT.sendPingRequest();
        }

        frameCounter++;
    }

    public LocalPlayerController getLocalPlayerController() {
        return localPlayerController;
    }

    public void start() {
        world.setPlaying(true);
        world.setVisible(true);
    }

    public void stop() {
        world.clear();
        world.setVisible(false);
    }
}
