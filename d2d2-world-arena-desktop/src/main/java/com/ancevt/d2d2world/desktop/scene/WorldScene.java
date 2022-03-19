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
package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.control.LocalPlayerController;
import com.ancevt.d2d2world.debug.GameObjectTexts;
import com.ancevt.d2d2world.desktop.ClientCommandProcessor;
import com.ancevt.d2d2world.desktop.DesktopConfig;
import com.ancevt.d2d2world.desktop.ui.UiText;
import com.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.net.client.ClientListenerAdapter;
import com.ancevt.d2d2world.net.dto.client.MapLoadedReport;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.sync.SyncDataReceiver;
import com.ancevt.d2d2world.sync.SyncMotion;
import com.ancevt.d2d2world.world.Overlay;
import com.ancevt.d2d2world.world.World;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static com.ancevt.d2d2world.desktop.ClientCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.desktop.DesktopConfig.*;
import static com.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static com.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;

@Slf4j
public class WorldScene extends DisplayObjectContainer {

    private final World world;
    private final LocalPlayerController localPlayerController = new LocalPlayerController();
    private Overlay overlay;
    private final ShadowRadial shadowRadial;
    private boolean eventsAdded;

    private long frameCounter;
    private PlayerActor localPlayerActor;
    private final GameObjectTexts gameObjectTexts;

    public WorldScene() {
        MapIO.mapsDirectory = "data/maps/";
        MapIO.mapkitsDirectory = "data/mapkits/";

        world = new World();
        gameObjectTexts = new GameObjectTexts(world);

        world.getPlayProcessor().setEnabled(false);
        world.getCamera().setBoundsLock(true);
        world.setVisible(false);
        world.setAlpha(MODULE_CONFIG.getFloat(DEBUG_WORLD_ALPHA));
        add(world);

        shadowRadial = new ShadowRadial() {
            @Override
            public void onEachFrame() {
                if (localPlayerActor != null) {
                    setXY(localPlayerActor.getX() + 35, localPlayerActor.getY());

                    if (world.getRoom() != null) {
                        if (getY() > world.getRoom().getHeight() + world.getRoom().getHeight() / 2f) {
                            setY(world.getRoom().getHeight() + world.getRoom().getHeight() / 2f);
                        }
                    }
                }
            }
        };
        shadowRadial.setScale(2f, 2f);
        world.add(shadowRadial);

        ((SyncDataReceiver) MODULE_CLIENT.getSyncDataReceiver()).setWorld(world);

        setScale(2f, 2f);

        localPlayerController.setEnabled(true);

        addEventListener(getClass(), Event.ADD_TO_STAGE, this::this_addToStage);

        MODULE_CLIENT.addClientListener(new ClientListenerAdapter() {

            @Override
            public void serverInfo(@NotNull ServerInfoDto result) {
                result.getPlayers().forEach(p -> {
                    if (world.getGameObjectById(p.getPlayerActorGameObjectId()) instanceof PlayerActor playerActor) {
                        playerActorUiText(playerActor, p.getId(), p.getName());
                    }
                });
            }
        });

        MODULE_CONFIG.addConfigChangeListener(this::config_configChangeListener);

        config_configChangeListener(DEBUG_GAME_OBJECT_IDS, MODULE_CONFIG.getBoolean(DEBUG_GAME_OBJECT_IDS));

        MODULE_COMMAND_PROCESSOR.getCommands().add(new ClientCommandProcessor.Command(
                "//gameobjectids",
                args -> {
                    StringBuilder sb = new StringBuilder();
                    world.getGameObjects().forEach(o -> sb.append(o.getGameObjectId()).append(','));
                    MODULE_CHAT.addMessage(sb.toString());
                    return true;
                }
        ));
        MODULE_COMMAND_PROCESSOR.getCommands().add(new ClientCommandProcessor.Command(
                "//gameobjectnames",
                args -> {
                    StringBuilder sb = new StringBuilder();
                    world.getGameObjects().forEach(o -> sb.append(o.getName()).append(','));
                    MODULE_CHAT.addMessage(sb.toString());
                    return true;
                }
        ));
    }

    private void config_configChangeListener(@NotNull String key, Object value) {
        if (Objects.equals(key, DesktopConfig.DEBUG_GAME_OBJECT_IDS)) {
            var v = Boolean.parseBoolean(value.toString());

            if (v) {
                if (!gameObjectTexts.hasParent()) {
                    gameObjectTexts.setEnabled(true);
                    world.add(gameObjectTexts);
                }
            } else {
                gameObjectTexts.setEnabled(false);
                gameObjectTexts.removeFromParent();
            }

        }
    }

    private void this_addToStage(Event event) {
        removeEventListeners(getClass());
        final float w = getStage().getStageWidth();
        final float h = getStage().getStageHeight();
        overlay = new Overlay(w, h);
        setXY(w / 2, h / 2);
        add(overlay, -w / 2, -h / 2);
        world.getCamera().setViewportSize(w, h);
        world.getCamera().setBoundsLock(true);
    }

    public void init() {
        world.clear();
    }

    public void loadMap(String mapFilename) {
        world.clear();
        overlay.startIn();
        Lock lock = new Lock();
        overlay.addEventListener(Event.CHANGE, Event.CHANGE, event -> {
            if (overlay.getState() == Overlay.STATE_BLACK) {
                lock.unlockIfLocked();
                overlay.removeEventListeners(Event.CHANGE);
            }
        });
        lock.lock();

        MODULE_CLIENT.getSyncDataReceiver().setEnabled(false);

        MapkitManager.getInstance().disposeExternalMapkits();

        world.getPlayProcessor().setEnabled(false);
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
        world.getPlayProcessor().setEnabled(true);
        addRootAndChatEventsIfNotYet();

        start();

        dispatchEvent(SceneEvent.builder()
                .type(SceneEvent.MAP_LOADED)
                .build());

        MODULE_CLIENT.sendDto(MapLoadedReport.INSANCE);
        MODULE_CLIENT.getSyncDataReceiver().setEnabled(true);

        overlay.startOut();
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
                if (e.getKeyCode() == KeyCode.F11) {
                    if (shadowRadial.getDarknessValue() == 0) return;
                    shadowRadial.setDarknessValue(shadowRadial.getDarknessValue() - 1);
                } else if (e.getKeyCode() == KeyCode.F12) {
                    shadowRadial.setDarknessValue(shadowRadial.getDarknessValue() + 1);
                }
                if (e.getKeyCode() == KeyCode.F9) {
                    overlay.startIn();
                }
                if (e.getKeyCode() == KeyCode.F10) {
                    overlay.startOut();
                }
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

    public void playerActorUiText(PlayerActor playerActor, int playerId, String playerName) {
        UiText uiText = new UiText(playerName + "(" + playerId + ")");
        uiText.setScale(0.5f, 0.5f);
        playerActor.add(uiText, (-uiText.getTextWidth() / 2) * uiText.getScaleX(), -32);
    }

    public void setLocalPlayerActorGameObjectId(int playerActorGameObjectId) {
        localPlayerActor = (PlayerActor) world.getGameObjectById(playerActorGameObjectId);
        localPlayerActor.setController(localPlayerController);
        localPlayerActor.setLocalPlayerActor(true);
        world.getCamera().setAttachedTo(localPlayerActor);

        playerActorUiText(localPlayerActor, MODULE_CLIENT.getLocalPlayerId(), MODULE_CLIENT.getLocalPlayerName());
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
