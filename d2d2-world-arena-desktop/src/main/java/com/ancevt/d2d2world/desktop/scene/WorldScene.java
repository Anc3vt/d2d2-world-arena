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
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.control.LocalPlayerController;
import com.ancevt.d2d2world.debug.GameObjectTexts;
import com.ancevt.d2d2world.desktop.ClientCommandProcessor;
import com.ancevt.d2d2world.desktop.DesktopConfig;
import com.ancevt.d2d2world.desktop.scene.charselect.CharSelectScene;
import com.ancevt.d2d2world.desktop.ui.UiText;
import com.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import com.ancevt.d2d2world.desktop.ui.hud.AmmunitionHud;
import com.ancevt.d2d2world.gameobject.ActorEvent;
import com.ancevt.d2d2world.gameobject.DefaultMaps;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.net.client.ClientListenerAdapter;
import com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto;
import com.ancevt.d2d2world.net.dto.client.PlayerReadyToSpawnDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.sync.SyncDataReceiver;
import com.ancevt.d2d2world.sync.SyncMotion;
import com.ancevt.d2d2world.world.Overlay;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.desktop.ClientCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.desktop.DesktopConfig.*;
import static com.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static com.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;
import static com.ancevt.d2d2world.net.client.PlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto.CLOSE;
import static com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto.OPEN;

@Slf4j
public class WorldScene extends DisplayObjectContainer {

    private final World world;
    private final LocalPlayerController localPlayerController = new LocalPlayerController();
    private final AmmunitionHud ammunitionHud;
    private Overlay overlay;
    private final ShadowRadial shadowRadial;
    private boolean eventsAdded;

    private long frameCounter;
    private PlayerActor localPlayerActor;
    private final GameObjectTexts gameObjectTexts;
    private final Map<Integer, PlayerActor> playerActorMap;

    public WorldScene() {
        MapIO.mapsDirectory = "data/maps/";
        MapIO.mapkitsDirectory = "data/mapkits/";

        playerActorMap = new HashMap<>();

        world = new World();

        world.addEventListener(hashCode() + WorldEvent.PLAYER_ACTOR_TAKE_BULLET, WorldEvent.PLAYER_ACTOR_TAKE_BULLET, this::world_playerActorTakeBullet);

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
        shadowRadial.setColor(Color.YELLOW);
        shadowRadial.setScale(2f, 2f);
        //world.add(shadowRadial);


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
                        playerActorMap.put(p.getId(), playerActor);
                    }
                });
            }


        });

        MODULE_CONFIG.addConfigChangeListener(this::config_configChangeListener);

        config_configChangeListener(DEBUG_GAME_OBJECT_IDS, MODULE_CONFIG.getBoolean(DEBUG_GAME_OBJECT_IDS));

        MODULE_CHAT.addEventListener(ChatEvent.CHAT_INPUT_OPEN, event -> {
            MODULE_CLIENT.sendDto(PlayerChatEventDto.builder()
                    .playerId(MODULE_CLIENT.getLocalPlayerId())
                    .action(OPEN)
                    .build());
        });

        MODULE_CHAT.addEventListener(ChatEvent.CHAT_INPUT_CLOSE, event -> {
            MODULE_CLIENT.sendDto(PlayerChatEventDto.builder()
                    .playerId(MODULE_CLIENT.getLocalPlayerId())
                    .action(CLOSE)
                    .build());
        });

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
        MODULE_COMMAND_PROCESSOR.getCommands().add(new ClientCommandProcessor.Command(
                "//config",
                args -> {
                    String key = args.get(String.class, "-k");
                    String value = args.get(String.class, "-v");
                    if (key != null) {
                        MODULE_CHAT.addMessage(key + "=" + MODULE_CONFIG.getString(key), Color.DARK_GRAY);
                    }
                    if (key != null && value != null) {
                        MODULE_CONFIG.setProperty(key, value);
                        MODULE_CHAT.addMessage(key + "=" + MODULE_CONFIG.getString(key), Color.DARK_GREEN);
                    }
                    if (key == null && value == null) {
                        MODULE_CHAT.addMessage(MODULE_CONFIG.passwordSafeToString());
                    }
                    return true;
                }
        ));
        MODULE_COMMAND_PROCESSOR.getCommands().add(new ClientCommandProcessor.Command(
                "//fullscreen",
                args -> {

                    D2D2.setFullscreen(args.get(Boolean.class, 1));

                    return true;
                }
        ));
        ammunitionHud = new AmmunitionHud();
    }

    private void world_playerActorTakeBullet(Event event) {
        var e = (WorldEvent) event;
        MODULE_CLIENT.sendDamageReport(e.getBullet().getDamagingPower(), e.getBullet().getGameObjectId());
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
        removeEventListener(getClass());
        final float w = getStage().getStageWidth();
        final float h = getStage().getStageHeight();
        overlay = new Overlay(w, h);
        setXY(w / 2, h / 2);
        add(overlay, -w / 2, -h / 2);
        world.getCamera().setViewportSize(w, h);
        world.getCamera().setBoundsLock(true);

        ammunitionHud.setScale(3, 3);
        getParent().add(ammunitionHud, getStage().getStageWidth() - (32 + (8 * 4)) * ammunitionHud.getScaleX(), 0);
    }

    public void init() {
        world.clear();
    }

    public void loadMap(String mapFilename) {
        DefaultMaps.clear();
        IdGenerator.INSTANCE.clear();
        world.clear();
        overlay.startIn();
        Lock lock = new Lock();
        overlay.addEventListener(Event.CHANGE, Event.CHANGE, event -> {
            if (overlay.getState() == Overlay.STATE_BLACK) {
                lock.unlockIfLocked();
                overlay.removeEventListener(Event.CHANGE);
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

        CharSelectScene charSelectScene = new CharSelectScene();
        charSelectScene.addEventListener(CharSelectScene.CharSelectSceneEvent.CHARACTER_SELECT, event -> {
            var e = (CharSelectScene.CharSelectSceneEvent) event;
            var mapkitName = e.getMapkitItem().getMapkit().getName();
            var mapkitItemName = e.getMapkitItem().getName();

            addRootAndChatEventsIfNotYet();

            MODULE_CLIENT.getSyncDataReceiver().setEnabled(true);

            overlay.startOut();
            gameObjectTexts.clear();

            world.getCamera().setAttachedTo(localPlayerActor);

            world.add(D2D2World.getAim());

            start();

            MODULE_CLIENT.sendDto(PlayerReadyToSpawnDto.builder()
                    .mapkitName(mapkitName)
                    .mapkitItemName(mapkitItemName)
                    .build()
            );

            dispatchEvent(SceneEvent.builder()
                    .type(SceneEvent.MAP_LOADED)
                    .build());

            Mouse.setVisible(false);
        });
        getRoot().add(charSelectScene);
    }

    private void addRootAndChatEventsIfNotYet() {
        if (!eventsAdded) {

            getRoot().addEventListener(this, InputEvent.MOUSE_DOWN, event -> {
                var e = (InputEvent) event;
                if (localPlayerActor != null) {
                    final int oldState = localPlayerController.getState();
                    localPlayerController.setB(true);
                    if (oldState != localPlayerController.getState()) {
                        MODULE_CLIENT.sendLocalPlayerController(localPlayerController.getState());
                    }
                }
            });

            getRoot().addEventListener(this, InputEvent.MOUSE_UP, event -> {
                var e = (InputEvent) event;
                if (localPlayerActor != null) {
                    final int oldState = localPlayerController.getState();
                    localPlayerController.setB(false);
                    if (oldState != localPlayerController.getState()) {
                        MODULE_CLIENT.sendLocalPlayerController(localPlayerController.getState());
                    }
                }
            });

            getRoot().addEventListener(InputEvent.MOUSE_WHEEL, event -> {
                var e = (InputEvent) event;
                int delta = e.getDelta();
                MODULE_CLIENT.sendLocalPlayerWeaponSwitch(delta);
            });

            getRoot().addEventListener(InputEvent.KEY_DOWN, event -> {
                var e = (InputEvent) event;
                final int oldState = localPlayerController.getState();
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), true);

                if (oldState != localPlayerController.getState()) {
                    MODULE_CLIENT.sendLocalPlayerController(localPlayerController.getState());
                }
                if (e.getKeyCode() == KeyCode.F11) {
                    if (shadowRadial.getValue() == 0) return;
                    shadowRadial.setValue(shadowRadial.getValue() - 1);
                } else if (e.getKeyCode() == KeyCode.F12) {
                    shadowRadial.setValue(shadowRadial.getValue() + 1);
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

    /**
     * Called from {@link GameRoot}
     */
    public void playerChatEvent(int playerId, String action) {
        getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> {

            ChatHint chatHint = (ChatHint) playerActor.extra().get(ChatHint.class.getName());
            if (chatHint == null) {
                chatHint = new ChatHint() {
                    @Override
                    public void onEachFrame() {
                        super.onEachFrame();
                        setXY(playerActor.getX() - this.getWidth() / 4, playerActor.getY() - 48);
                    }
                };
                ChatHint finalChatHint = chatHint;
                playerActor.addEventListener(Event.REMOVE_FROM_STAGE, event -> {
                    finalChatHint.removeFromParent();
                });
                chatHint.setScale(0.5f, 0.5f);
                playerActor.extra().put(ChatHint.class.getName(), chatHint);
            }

            switch (action) {
                case OPEN -> world.add(chatHint);
                case CLOSE -> chatHint.removeFromParent();
            }
        });
    }

    /**
     * Called from {@link GameRoot}
     */
    public void setLocalPlayerActorGameObjectId(int playerActorGameObjectId) {
        localPlayerActor = (PlayerActor) world.getGameObjectById(playerActorGameObjectId);

        if (localPlayerActor == null) {
            Async.runLater(1, TimeUnit.SECONDS, () -> setLocalPlayerActorGameObjectId(playerActorGameObjectId));
            return;
        }

        localPlayerActor.setName("lpa");
        localPlayerActor.addEventListener(ActorEvent.AMMUNITION_CHANGE, event -> ammunitionHud.updateFor(localPlayerActor));
        localPlayerActor.addEventListener(ActorEvent.SET_WEAPON, event -> ammunitionHud.updateFor(localPlayerActor));
        localPlayerActor.addEventListener(ActorEvent.ACTOR_DEATH, event -> overlay.startIn());
        localPlayerActor.addEventListener(ActorEvent.ACTOR_REPAIR, event -> {
            world.getCamera().setXY(localPlayerActor.getX(), localPlayerActor.getY());
            overlay.startOut();
        });
        localPlayerActor.addEventListener(Event.EACH_FRAME, new EventListener() {

            private float aimX;
            private float aimY;

            @Override
            public void onEvent(Event event) {
                float currentAimX = localPlayerActor.getAimX();
                float currentAimY = localPlayerActor.getAimY();

                if (currentAimX != aimX || currentAimY != aimY) {
                    MODULE_CLIENT.sendAimXY(currentAimX, currentAimY);
                }

                aimX = localPlayerActor.getAimX();
                aimY = localPlayerActor.getAimY();
            }
        });

        localPlayerActor.setController(localPlayerController);
        localPlayerActor.setLocalPlayerActor(true);
        localPlayerActor.setLocalAim(true);
        world.getCamera().setAttachedTo(localPlayerActor);
        playerActorUiText(localPlayerActor, MODULE_CLIENT.getLocalPlayerId(), MODULE_CLIENT.getLocalPlayerName());
    }

    public void playerActorUiText(@NotNull PlayerActor playerActor, int playerId, String playerName) {
        UiText uiText = new UiText(playerName + "(" + playerId + ")");
        uiText.setScale(0.5f, 0.5f);
        PLAYER_MANAGER.getPlayer(playerId).ifPresent(player -> uiText.setColor(Color.of(player.getColor())));
        playerActor.add(uiText, (-uiText.getTextWidth() / 2) * uiText.getScaleX(), -32);
    }

    private Optional<PlayerActor> getPlayerActorByPlayerId(int playerId) {
        return Optional.ofNullable(playerActorMap.get(playerId));
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

        if (localPlayerActor != null) {
            D2D2World.getAim().setXY(localPlayerActor.getAimX(), localPlayerActor.getAimY());
        }
        frameCounter++;
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
