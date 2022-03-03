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
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2world.control.Controller;
import ru.ancevt.d2d2world.control.LocalPlayerController;
import ru.ancevt.d2d2world.desktop.ui.UiText;
import ru.ancevt.d2d2world.desktop.ui.chat.Chat;
import ru.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import ru.ancevt.d2d2world.gameobject.PlayerActor;
import ru.ancevt.d2d2world.gameobject.character.Blake;
import ru.ancevt.d2d2world.map.MapIO;
import ru.ancevt.d2d2world.net.client.Client;
import ru.ancevt.d2d2world.net.client.RemotePlayer;
import ru.ancevt.d2d2world.world.World;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.ancevt.d2d2world.desktop.ModuleContainer.modules;

@Slf4j
public class WorldScene extends DisplayObjectContainer {

    private final World world = new World();
    private final LocalPlayerController localPlayerController = new LocalPlayerController();
    private final Client client = modules.get(Client.class);
    private final Chat chat = modules.get(Chat.class);
    private final PlayerActor localPlayerActor;
    private final BitmapText debug;
    private boolean eventsAdded;

    private final Map<RemotePlayer, PlayerActor> remotePlayerMap;

    private long frameCounter;

    public WorldScene() {
        localPlayerActor = new Blake();
        localPlayerActor.setController(localPlayerController);
        localPlayerController.setEnabled(true);

        world.getCamera().setAttachedTo(localPlayerActor);
        world.getCamera().setBoundsLock(true);
        world.setVisible(false);

        remotePlayerMap = new ConcurrentHashMap<>();

        setScale(2f, 2f);

        add(world);

        debug = new BitmapText();
        debug.setText("debug");
    }

    public void init() {
        world.clear();

        Async.run(() -> {
            try {
                long timeBefore = System.currentTimeMillis();
                world.setMap(MapIO.load("map0.wam"));
                mapLoaded();
                log.info("Map loaded {}ms", (System.currentTimeMillis() - timeBefore));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });

        System.out.println("WC: reinit");
    }

    private void mapLoaded() {
        world.setSceneryPacked(true);

        localPlayerActor.reset();
        localPlayerActor.setXY(16, 16);
        world.addGameObject(localPlayerActor, 5, false);
        world.getCamera().setViewportSize(getStage().getStageWidth(), getStage().getStageHeight());

        setXY(getStage().getStageWidth() / 2, getStage().getStageHeight() / 2);

        //getRoot().add(debug, 10, 250);

        addRootAndChatEventsIfNotYet();

        remotePlayerMap.values().forEach(playerActor -> {
            if (!playerActor.hasParent()) {
                world.addGameObject(playerActor, 5, false);
            }
        });

        dispatchEvent(new SceneEvent(SceneEvent.MAP_LOADED, this));
    }

    private void addRootAndChatEventsIfNotYet() {
        if (!eventsAdded) {
            getRoot().addEventListener(InputEvent.KEY_DOWN, event -> {
                var e = (InputEvent) event;
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), true);
            });
            getRoot().addEventListener(InputEvent.KEY_UP, event -> {
                var e = (InputEvent) event;
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), false);
            });

            chat.addEventListener(ChatEvent.CHAT_INPUT_OPEN, e -> localPlayerController.setEnabled(false));
            chat.addEventListener(ChatEvent.CHAT_INPUT_CLOSE, e -> localPlayerController.setEnabled(true));
            eventsAdded = true;
        }
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();
        if (!client.isConnected() || !client.isEnteredServer()) return;

        client.sendLocalPlayerControllerAndXYReport(
                localPlayerController.getState(),
                localPlayerActor.getX(),
                localPlayerActor.getY()
        );

        remotePlayerMap.forEach((remotePlayer, remotePlayerActor) -> {
            remotePlayerActor.setXY(remotePlayer.getX(), remotePlayer.getY());
            remotePlayerActor.getController().applyState(remotePlayer.getControllerState());
        });

        debug.setText(remotePlayerMap + "");

        if (frameCounter % 1000 == 0) {
            client.sendServerInfoRequest();
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

    public void addRemotePlayer(RemotePlayer remotePlayer) {


        log.trace("Add remote player {}", remotePlayer);

        UiText uiText = new UiText();
        uiText.setShadowEnabled(false);
        uiText.setScale(0.5f, 0.5f);
        uiText.setText(remotePlayer.getName() + "(" + remotePlayer.getId() + ")");

        PlayerActor playerActor = new Blake();

        chat.addMessage("");


        playerActor.add(uiText, -uiText.getTextWidth() / 4, -30f);
        Controller controller = new Controller();
        controller.setEnabled(true);
        playerActor.setController(controller);

        remotePlayerMap.put(remotePlayer, playerActor);
        world.addGameObject(playerActor, 5, false);

    }

    public void removeRemotePlayer(RemotePlayer remotePlayer) {
        log.trace("Remove remote player {}", remotePlayer);

        PlayerActor playerActor = remotePlayerMap.get(remotePlayer);
        if (playerActor != null) {
            world.removeGameObject(playerActor, false);
            remotePlayerMap.remove(remotePlayer);
        }
    }
}
