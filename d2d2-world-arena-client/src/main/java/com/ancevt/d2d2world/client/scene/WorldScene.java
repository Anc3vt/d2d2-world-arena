
package com.ancevt.d2d2world.client.scene;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.control.LocalPlayerController;
import com.ancevt.d2d2world.debug.GameObjectTexts;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;
import com.ancevt.d2d2world.client.net.ClientListener;
import com.ancevt.d2d2world.client.scene.charselect.CharSelectScene;
import com.ancevt.d2d2world.client.settings.DesktopConfig;
import com.ancevt.d2d2world.client.settings.MonitorDevice;
import com.ancevt.d2d2world.client.ui.UiText;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.d2d2world.client.ui.chat.ChatEvent;
import com.ancevt.d2d2world.client.ui.hud.AmmunitionHud;
import com.ancevt.d2d2world.client.ui.playerarrowview.PlayerArrowView;
import com.ancevt.d2d2world.gameobject.ActorEvent;
import com.ancevt.d2d2world.gameobject.DefaultMaps;
import com.ancevt.d2d2world.gameobject.DestroyableBox;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto;
import com.ancevt.d2d2world.net.dto.client.PlayerReadyToSpawnDto;
import com.ancevt.d2d2world.net.dto.client.RoomSwitchCompleteDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.sound.D2D2WorldSound;
import com.ancevt.d2d2world.sync.SyncDataReceiver;
import com.ancevt.d2d2world.sync.SyncMotion;
import com.ancevt.d2d2world.world.Overlay;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.client.ClientCommandProcessor.COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.client.net.Client.CLIENT;
import static com.ancevt.d2d2world.client.net.PlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.client.settings.DesktopConfig.CONFIG;
import static com.ancevt.d2d2world.client.settings.DesktopConfig.DEBUG_GAME_OBJECT_IDS;
import static com.ancevt.d2d2world.client.settings.DesktopConfig.DEBUG_WORLD_ALPHA;
import static com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto.CLOSE;
import static com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto.OPEN;
import static com.ancevt.d2d2world.sound.D2D2WorldSound.PLAYER_SPAWN;

@Slf4j
public class WorldScene extends DisplayObjectContainer implements ClientListener {

    private final World world;
    private final LocalPlayerController localPlayerController = new LocalPlayerController();
    private final AmmunitionHud ammunitionHud;
    private Overlay overlay;
    private boolean eventsAdded;

    private long frameCounter;
    private PlayerActor localPlayerActor;
    private final GameObjectTexts gameObjectTexts;
    private final Map<Integer, PlayerActor> playerIdPlayerActorMap;
    private final Map<Integer, Integer> playerActorGameObjectIdPlayerIdMap;

    private List<Weapon> roomChangePlayerActorWeapons;
    private Weapon roomChangePlayerActorCurrentWeapon;

    private final Set<ChatBubble> chatBubbles;

    private final PlayerArrowView playerArrowView;

    public WorldScene() {
        MapIO.setMapsDirectory("data/maps/");
        MapIO.setMapkitsDirectory("data/mapkits/");

        playerIdPlayerActorMap = new HashMap<>();
        playerActorGameObjectIdPlayerIdMap = new HashMap<>();

        chatBubbles = new HashSet<>();

        world = createWorld();
        add(world);

        gameObjectTexts = new GameObjectTexts(world);

        playerArrowView = new PlayerArrowView();
        //playerArrowView.setScale(D2D2World.SCALE, D2D2World.SCALE);

        ((SyncDataReceiver) CLIENT.getSyncDataReceiver()).setWorld(world);

        setScale(D2D2World.SCALE, D2D2World.SCALE);

        localPlayerController.setEnabled(true);

        addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);

        CLIENT.addClientListener(this);

        CLIENT.addClientListener(new ClientListener() {

            @Override
            public void serverInfo(@NotNull ServerInfoDto result) {
                result.getPlayers().forEach(player -> {
                    if (world.getGameObjectById(player.getPlayerActorGameObjectId()) instanceof PlayerActor playerActor) {
                        playerActorUiText(playerActor, player.getId(), player.getName());
                        playerIdPlayerActorMap.put(player.getId(), playerActor);
                        PLAYER_MANAGER.getPlayerById(player.getId()).ifPresent(
                                playerManagerPlayer -> playerManagerPlayer.setPlayerActorGameObjectId(playerActor.getGameObjectId())
                        );
                    }
                });
            }


        });

        CONFIG.addConfigChangeListener(this::config_configChangeListener);

        config_configChangeListener(DEBUG_GAME_OBJECT_IDS, CONFIG.getBoolean(DEBUG_GAME_OBJECT_IDS));

        Chat.getInstance().addEventListener(ChatEvent.CHAT_INPUT_OPEN, event -> {
            CLIENT.sendDto(PlayerChatEventDto.builder()
                    .playerId(CLIENT.getLocalPlayerId())
                    .action(OPEN)
                    .build());
        });

        Chat.getInstance().addEventListener(ChatEvent.CHAT_INPUT_CLOSE, event -> {
            CLIENT.sendDto(PlayerChatEventDto.builder()
                    .playerId(CLIENT.getLocalPlayerId())
                    .action(CLOSE)
                    .build());
        });

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//tostring",
                "print game object info by game object id",
                args -> {
                    IGameObject gameObject = world.getGameObjectById(args.get(int.class, 1));
                    if (gameObject == null) {
                        Chat.getInstance().addMessage("no such game object", Color.YELLOW);
                        return null;
                    }
                    Chat.getInstance().addMessage(gameObject.toString() + "\n"
                            + getProperties(gameObject) + "\n"
                            + gameObject.getMapkitItem().getDataEntry().toString(), Color.YELLOW
                    );
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//gameobjectids",
                "print list of game object ids",
                args -> {
                    StringBuilder sb = new StringBuilder();
                    world.getGameObjects().forEach(o -> sb.append(o.getGameObjectId()).append(','));
                    Chat.getInstance().addMessage(sb.toString(), Color.YELLOW);
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//gameobjectnames",
                "print list of game object names",
                args -> {
                    StringBuilder sb = new StringBuilder();
                    world.getGameObjects().forEach(o -> sb.append(o.getName()).append(','));
                    Chat.getInstance().addMessage(sb.toString(), Color.YELLOW);
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//config",
                "print client config [[-k [-v]]]",
                args -> {
                    String key = args.get(String.class, "-k");
                    String value = args.get(String.class, "-v");
                    if (key != null) {
                        Chat.getInstance().addMessage(key + "=" + CONFIG.getString(key), Color.DARK_GRAY);
                    }
                    if (key != null && value != null) {
                        CONFIG.setProperty(key, value);
                        Chat.getInstance().addMessage(key + "=" + CONFIG.getString(key), Color.DARK_GREEN);
                    }
                    if (key == null && value == null) {
                        Chat.getInstance().addMessage(CONFIG.passwordSafeToString(), Color.YELLOW);
                    }
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//monitorlist",
                "print list of avialable monitors",
                args -> {
                    LWJGLVideoModeUtils.getMonitors().values().forEach(
                            monitorName -> Chat.getInstance().addMessage(monitorName)
                    );
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//videomodelist",
                "print list of video modes",
                args -> {
                    LWJGLVideoModeUtils.getVideoModes(MonitorDevice.getInstance().getMonitorDeviceId()).forEach(videoMode ->
                            Chat.getInstance().addMessage(videoMode.getWidth() + "x" + videoMode.getHeight() + " " + videoMode.getRefreshRate())
                    );
                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//resolution",
                "set video mode [<width>x<height>]",
                args -> {
                    String resolution = args.get(String.class, 1, "0x0");
                    Holder<Boolean> found = new Holder<>(false);
                    LWJGLVideoModeUtils.getVideoModes(MonitorDevice.getInstance().getMonitorDeviceId()).forEach(videoMode -> {
                        if (videoMode.getResolution().equals(resolution) && videoMode.getRefreshRate() == 60) {
                            found.setValue(true);
                            Chat.getInstance().addMessage(resolution + " " + videoMode.getRefreshRate());
                            MonitorDevice.getInstance().setResolution(resolution);
                            MonitorDevice.getInstance().setFullscreen(true);
                        }
                    });

                    if (!found.getValue()) {
                        Chat.getInstance().addMessage("vid mode not found");
                    }

                    return null;
                }
        );

        COMMAND_PROCESSOR.getCommandSet().registerCommand(
                "//cls",
                "clear chat",
                args -> {
                    Chat.getInstance().clear();
                    return null;
                }
        );

        ammunitionHud = new AmmunitionHud();
    }

    private @NotNull World createWorld() {
        var world = new World();
        world.addEventListener(this, WorldEvent.PLAYER_ACTOR_TAKE_BULLET, this::world_playerActorTakeBullet);
        world.addEventListener(this, WorldEvent.ROOM_SWITCH_COMPLETE, this::world_roomSwitchComplete);
        world.addEventListener(this, WorldEvent.ADD_GAME_OBJECT, this::world_addGameObject);
        world.addEventListener(this, WorldEvent.REMOVE_GAME_OBJECT, this::world_removeGameObject);
        world.addEventListener(this, WorldEvent.ACTOR_DEATH, this::world_actorDeath);

        world.getPlayProcessor().setAsyncProcessingEnabled(false);
        world.getCamera().setBoundsLock(true);
        world.setVisible(false);
        world.setAlpha(CONFIG.getFloat(DEBUG_WORLD_ALPHA));

        return world;
    }

    private void world_actorDeath(Event<World> event) {
        var e = (WorldEvent) event;
        if (world.getGameObjectById(e.getDeadActorGameObjectId()) instanceof PlayerActor playerActor) {
            playerArrowView.removePlayerArrow(playerActor);
        }
    }

    public void resize(float w, float h) {
        setXY(w / 2, h / 2);

        float scale = h / D2D2World.ORIGIN_HEIGHT;
        setScaleY(scale);
        toScaleY(D2D2World.SCALE);
        setScaleX(getScaleY());

        overlay.setXY(-w / 2, -h / 2);
        overlay.setSize(w, h);

        playerArrowView.setXY(0, 0);
        playerArrowView.setScale(getScaleX(), getScaleY());

        playerArrowView.setViewport(
                w / playerArrowView.getAbsoluteScaleX(),
                h / playerArrowView.getAbsoluteScaleY()
        );

        ammunitionHud.setXY(w - (32 + (8 * 4)) * ammunitionHud.getScaleX(), 0);

        world.getCamera().setViewportSize(w, h);
    }

    private void this_addToStage(Event event) {
        removeEventListener(this, Event.ADD_TO_STAGE);

        float w = D2D2.getStage().getWidth();
        float h = D2D2.getStage().getHeight();

        overlay = new Overlay(w, h);
        setXY(w / 2, h / 2);

        add(overlay, -w / 2, -h / 2);
        world.getCamera().setViewportSize(w, h);
        world.getCamera().setBoundsLock(true);

        ammunitionHud.setScale(3, 3);
        getParent().add(ammunitionHud, w - (32 + (8 * 4)) * ammunitionHud.getScaleX(), 0);
        playerArrowView.setViewport(
                w / playerArrowView.getAbsoluteScaleX(),
                h / playerArrowView.getAbsoluteScaleY()
        );

        getParent().add(playerArrowView);

        float scale = h / D2D2World.ORIGIN_HEIGHT;
        setScaleY(scale);
        toScaleY(D2D2World.SCALE);
        setScaleX(getScaleY());
    }

    private void world_addGameObject(Event<World> event) {
        var e = (WorldEvent) event;
        if (e.getGameObject() instanceof PlayerActor playerActor) {
            PLAYER_MANAGER.getPlayerByPlayerActorGameObjectId(playerActor.getGameObjectId()).ifPresent(player -> {
                if (player.isChatOpened()) showChatBubble(playerActor);
            });

            if (!playerActor.isLocalPlayerActor()) {
                playerArrowView.createPlayerArrow(playerActor, playerActor.getPlayerColor());
            }

            playerActorUiText(playerActor, playerActor.getPlayerId(), playerActor.getPlayerName());

            if (localPlayerActor != null) {
                if (localPlayerActor.getGameObjectId() == playerActor.getGameObjectId()) {
                    setLocalPlayerActor(playerActor);
                }
            }
        }
    }

    private void world_removeGameObject(Event<World> event) {
        var e = (WorldEvent) event;
        if (e.getGameObject() instanceof PlayerActor playerActor) {
            hideChatBubble(playerActor);
            playerArrowView.removePlayerArrow(playerActor);
        }
    }

    private void world_roomSwitchComplete(Event<World> event) {
        clearChatBubbles();
        CLIENT.sendDto(RoomSwitchCompleteDto.builder().build());
    }

    private void world_playerActorTakeBullet(Event<World> event) {
        var e = (WorldEvent) event;
        CLIENT.sendDamageReport(e.getBullet().getDamagingPower(), e.getBullet().getGameObjectId());
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

    public void init() {
        world.clear();
    }

    public void loadMap(String mapFilename) {
        IdGenerator.getInstance().clear();
        world.clear();
        DefaultMaps.clear();
        overlay.startIn();
        Lock lock = new Lock();
        overlay.addEventListener(this, Event.CHANGE, event -> {
            if (overlay.getState() == Overlay.STATE_BLACK) {
                lock.unlockIfLocked();
                overlay.removeEventListener(this, Event.CHANGE);
            }
        });
        lock.lock();

        CLIENT.getSyncDataReceiver().setEnabled(false);

        MapkitManager.getInstance().disposeExternalMapkits();

        world.getPlayProcessor().setAsyncProcessingEnabled(false);
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
        world.getPlayProcessor().setAsyncProcessingEnabled(true);

        CharSelectScene charSelectScene = new CharSelectScene();
        charSelectScene.addEventListener(CharSelectScene.CharSelectSceneEvent.CHARACTER_SELECT, event -> {
            var e = (CharSelectScene.CharSelectSceneEvent) event;
            var mapkitName = e.getMapkitItem().getMapkit().getName();
            var mapkitItemId = e.getMapkitItem().getId();

            addRootAndChatEventsIfNotYet();

            CLIENT.getSyncDataReceiver().setEnabled(true);

            overlay.startOut();
            gameObjectTexts.clear();

            world.getCamera().setAttachedTo(localPlayerActor);
            world.setSceneryPacked(true);
            world.add(D2D2WorldArenaDesktopAssets.getAim());

            start();

            CLIENT.sendDto(PlayerReadyToSpawnDto.builder()
                    .mapkitName(mapkitName)
                    .mapkitItemId(mapkitItemId)
                    .build()
            );

            Mouse.setVisible(false);

            ControlsHelp controlsHelp = new ControlsHelp();
            getRoot().add(controlsHelp,
                    (D2D2.getStage().getStageWidth() - controlsHelp.getWidth()) / 2,
                    (D2D2.getStage().getStageHeight() - controlsHelp.getHeight()) / 5
            );
        });
        getRoot().add(charSelectScene);
    }

    private void addRootAndChatEventsIfNotYet() {
        if (!eventsAdded) {

            getRoot().addEventListener(this, InputEvent.MOUSE_DOWN, event -> {
                if (localPlayerActor != null) {
                    final int oldState = localPlayerController.getState();
                    localPlayerController.setB(true);
                    if (oldState != localPlayerController.getState()) {
                        CLIENT.sendLocalPlayerController(localPlayerController.getState());
                    }
                }
            });

            getRoot().addEventListener(this, InputEvent.MOUSE_UP, event -> {
                if (localPlayerActor != null) {
                    final int oldState = localPlayerController.getState();
                    localPlayerController.setB(false);
                    if (oldState != localPlayerController.getState()) {
                        CLIENT.sendLocalPlayerController(localPlayerController.getState());
                    }
                }
            });

            getRoot().addEventListener(InputEvent.MOUSE_WHEEL, event -> {
                var e = (InputEvent) event;
                int delta = e.getDelta();
                CLIENT.sendLocalPlayerWeaponSwitch(delta);
            });

            getRoot().addEventListener(InputEvent.KEY_DOWN, event -> {
                var e = (InputEvent) event;
                final int oldState = localPlayerController.getState();
                localPlayerController.key(e.getKeyCode(), e.getKeyChar(), true);

                if (oldState != localPlayerController.getState()) {
                    CLIENT.sendLocalPlayerController(localPlayerController.getState());
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
                    CLIENT.sendLocalPlayerController(localPlayerController.getState());
                }
            });

            Chat.getInstance().addEventListener(ChatEvent.CHAT_INPUT_OPEN, e -> localPlayerController.setEnabled(false));
            Chat.getInstance().addEventListener(ChatEvent.CHAT_INPUT_CLOSE, e -> localPlayerController.setEnabled(true));
            eventsAdded = true;
        }
    }


    @Override
    public void mapContentLoaded(@NotNull String mapFilename) {
        loadMap(mapFilename);
    }

    /**
     * {@link ClientListener} method
     */
    public void playerChatEvent(int playerId, String action) {
        getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> {
            if (OPEN.equals(action))
                showChatBubble(playerActor);
            else
                hideChatBubble(playerActor);
        });
    }

    /**
     * {@link ClientListener} method
     */
    public void localPlayerActorGameObjectId(int playerActorGameObjectId) {
        PlayerActor playerActor = (PlayerActor) world.getGameObjectById(playerActorGameObjectId);

        if (playerActor == null) {
            Async.runLater(1, TimeUnit.SECONDS, () -> localPlayerActorGameObjectId(playerActorGameObjectId));
            return;
        }

        setLocalPlayerActor(playerActor);
    }

    /**
     * {@link ClientListener} method
     */
    public void playerEnterRoomStartResponseReceived() {
        world.roomSwitchOverlayStartOut();
    }

    /**
     * {@link ClientListener} method
     */
    public void playerDeath(int deadPlayerId, int killerPlayerId) {
        getPlayerActorByPlayerId(deadPlayerId).ifPresent(playerArrowView::removePlayerArrow);
    }

    /**
     * {@link ClientListener} method
     */
    public void setRoom(String roomId, float cameraX, float cameraY) {
        playerArrowView.clear();
        clearChatBubbles();
        world.setSceneryPacked(false);
        world.setRoom(world.getMap().getRoom(roomId));
        world.setSceneryPacked(true);
        world.getCamera().setXY(cameraX, cameraY);
        CLIENT.sendPlayerActorRequest();
    }

    /**
     * {@link ClientListener} method
     */
    public void spawnEffect(float x, float y) {
        SpawnEffect.doSpawnEffect(x, y, world.getLayer(5));
        D2D2WorldSound.playSoundAsset(PLAYER_SPAWN, world.getCamera(), x, y);
    }

    /**
     * {@link ClientListener} method
     */
    public void destroyableBoxDestroy(int destroyableGameObjectId) {
        if (world.getGameObjectById(destroyableGameObjectId) instanceof DestroyableBox destroyableBox) {
            destroyableBox.doDestroyEffect();
        }
    }

    /**
     * {@link ClientListener} method
     */
    public void playerShoot(int playerId) {
        if (playerId == CLIENT.getLocalPlayerId()) {
            getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> {
                D2D2WorldArenaDesktopAssets.getAim().attack();
            });
        }
    }

    private void showChatBubble(@NotNull PlayerActor playerActor) {
        ChatBubble chatBubble = (ChatBubble) playerActor.extra().get(ChatBubble.class.getName());
        if (chatBubble == null) {
            chatBubble = new ChatBubble() {
                @Override
                public void onEachFrame() {
                    super.onEachFrame();
                    setXY(playerActor.getX() - this.getWidth() / 4, playerActor.getY() - 48);
                }
            };
            playerActor.addEventListener(Event.REMOVE_FROM_STAGE, event -> {
                ChatBubble cb = (ChatBubble) playerActor.extra().get(ChatBubble.class.getName());
                if (cb != null) {
                    cb.removeFromParent();
                }
            });
            playerActor.extra().put(ChatBubble.class.getName(), chatBubble);
            chatBubbles.add(chatBubble);
            chatBubble.setScale(0.5f, 0.5f);
        }
        world.add(chatBubble);
    }

    private void hideChatBubble(@NotNull PlayerActor playerActor) {
        ChatBubble chatHint = (ChatBubble) playerActor.extra().get(ChatBubble.class.getName());
        if (chatHint != null) chatHint.removeFromParent();
    }

    private void clearChatBubbles() {
        chatBubbles.forEach(DisplayObject::removeFromParent);
        chatBubbles.clear();
    }

    private void setLocalPlayerActor(PlayerActor playerActor) {
        if (localPlayerActor == playerActor) return;

        localPlayerActor = playerActor;

        playerArrowView.setFrom(localPlayerActor);

        if (overlay.getState() == Overlay.STATE_BLACK) {
            world.getCamera().setXY(localPlayerActor.getX(), localPlayerActor.getY());
            overlay.startOut();
        }

        localPlayerActor.addEventListener(ActorEvent.AMMUNITION_CHANGE, event -> ammunitionHud.updateFor(localPlayerActor), true);
        localPlayerActor.addEventListener(ActorEvent.SET_WEAPON, event -> ammunitionHud.updateFor(localPlayerActor), true);
        localPlayerActor.addEventListener(ActorEvent.ACTOR_DEATH, event -> Async.runLater(2, TimeUnit.SECONDS, overlay::startIn), true);

        localPlayerActor.addEventListener(ActorEvent.ACTOR_ENTER_ROOM, event -> {
            var e = (ActorEvent) event;
            roomChangePlayerActorWeapons = e.getSource().getWeapons();
            roomChangePlayerActorCurrentWeapon = e.getSource().getCurrentWeapon();
            CLIENT.sendPlayerEnterRoom(e.getRoomId(), e.getX(), e.getY());
        });

        localPlayerActor.addEventListener(ActorEvent.ACTOR_REPAIR, event -> {
            world.getCamera().setXY(localPlayerActor.getX(), localPlayerActor.getY());
            overlay.startOut();
        });

        localPlayerActor.addEventListener(ActorEvent.ACTOR_HOOK, event -> {
            var e = (ActorEvent) event;
            if (e.getHookGameObjectId() != 0) {
                CLIENT.sendHook(e.getHookGameObjectId());
            } else {
                CLIENT.sendHook(0);
            }
        });

        localPlayerActor.addEventListener(Event.EACH_FRAME, new EventListener() {
            private float oldX;
            private float oldY;

            private float oldAimX;
            private float oldAimY;

            @Override
            public void onEvent(Event event) {
                float currentAimX = localPlayerActor.getAimX();
                float currentAimY = localPlayerActor.getAimY();

                if (currentAimX != oldAimX || currentAimY != oldAimY) CLIENT.sendAimXY(currentAimX, currentAimY);

                oldAimX = localPlayerActor.getAimX();
                oldAimY = localPlayerActor.getAimY();

                float currentX = localPlayerActor.getX();
                float currentY = localPlayerActor.getY();

                if (currentX != oldX || currentY != oldY) CLIENT.sendXY(currentX, currentY);

                oldX = localPlayerActor.getX();
                oldY = localPlayerActor.getY();
            }
        });

        localPlayerActor.setController(localPlayerController);
        localPlayerActor.setLocalPlayerActor(true);
        localPlayerActor.setLocalAim(true);
        world.getCamera().setAttachedTo(localPlayerActor);
        playerActorUiText(localPlayerActor, CLIENT.getLocalPlayerId(), CLIENT.getLocalPlayerName());

        if (roomChangePlayerActorWeapons != null) {
            localPlayerActor.setWeapons(roomChangePlayerActorWeapons);
            localPlayerActor.setCurrentWeaponClass(roomChangePlayerActorCurrentWeapon.getClass());
        }
    }

    public void playerActorUiText(@NotNull PlayerActor playerActor, int playerId, String playerName) {
        playerActor.setPlayerName(playerName);
        playerActor.setPlayerId(playerId);
        UiText uiText = new UiText(playerName + "(" + playerId + ")");
        uiText.setScale(0.5f, 0.5f);
        PLAYER_MANAGER.getPlayerById(playerId).ifPresent(player -> uiText.setColor(Color.of(player.getColor())));
        playerActor.add(uiText, (-uiText.getTextWidth() / 2) * uiText.getScaleX(), -32);
    }

    private Optional<PlayerActor> getPlayerActorByPlayerId(int playerId) {
        return Optional.ofNullable(playerIdPlayerActorMap.get(playerId));
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        if (!CLIENT.isConnected() || !CLIENT.isEnteredServer()) return;

        SyncMotion.process();

        if (frameCounter % 1000 == 0) {
            CLIENT.sendServerInfoRequest();
        }

        if (frameCounter % 250 == 0) {
            CLIENT.sendPingRequest();
        }

        if (localPlayerActor != null) {
            D2D2WorldArenaDesktopAssets.getAim().setXY(localPlayerActor.getAimX(), localPlayerActor.getAimY());
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
