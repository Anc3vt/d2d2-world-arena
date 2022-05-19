
package com.ancevt.d2d2world.client.scene;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import com.ancevt.d2d2world.client.net.ClientListener;
import com.ancevt.d2d2world.client.net.Player;
import com.ancevt.d2d2world.client.settings.ClientConfig;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.ui.TabWindow;
import com.ancevt.d2d2world.client.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.d2d2world.client.ui.chat.ChatEvent;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.sound.D2D2WorldSound;
import com.ancevt.net.CloseStatus;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static com.ancevt.d2d2world.client.ClientCommandProcessor.COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.client.net.Client.CLIENT;
import static com.ancevt.d2d2world.client.net.PlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.client.settings.ClientConfig.CONFIG;
import static com.ancevt.d2d2world.sound.D2D2WorldSound.PLAYER_ENTER;
import static com.ancevt.d2d2world.sound.D2D2WorldSound.PLAYER_EXIT;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class GameRoot extends Root implements ClientListener, FileReceiverManager.FileReceiverManagerListener {

    public static final int DEFAULT_PORT = 2245;

    // TODO: refactor
    public static GameRoot INSTANCE;

    private String server;
    private final WorldScene worldScene;
    private final TabWindow tabWindow;
    private String serverName;
    private int attempts;
    private boolean connected;

    public GameRoot() {
        UiTextInputProcessor.enableRoot(this);

        setBackgroundColor(Color.BLACK);

        CLIENT.addClientListener(this);

        Chat.getInstance().addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            var e = (ChatEvent) event;
            String text = e.getText();
            if (text.startsWith("/") || text.startsWith("\\")) {
                // if typed command is one of registered client command then don't send that command text to the server
                text = text.replace('\\', '/');
                if (clientCommand(text)) return;
            }
            if (CLIENT.isConnected()) {
                CLIENT.sendChatMessage(text);
            }
        });

        addEventListener(this, InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.PAGE_UP -> Chat.getInstance().setScroll(Chat.getInstance().getScroll() - 10);
                case KeyCode.PAGE_DOWN -> Chat.getInstance().setScroll(Chat.getInstance().getScroll() + 10);
                case KeyCode.F8 -> Chat.getInstance().setShadowEnabled(!Chat.getInstance().isShadowEnabled());
                case KeyCode.F6 -> {
                    if (!Chat.getInstance().isInputOpened()) {
                        Chat.getInstance().openInput();
                    } else {
                        Chat.getInstance().closeInput();
                    }
                }
                case KeyCode.TAB -> {
                    Chat.getInstance().setVisible(false);
                    setTabWindowVisible(true);
                }
                case KeyCode.F -> {
                    if (e.isAlt()) {
                        D2D2.setFullscreen(!D2D2.isFullscreen());
                    }
                }
                case KeyCode.S -> {
                    if (e.isAlt()) D2D2.setSmoothMode(!D2D2.isSmoothMode());
                }
                case KeyCode.T -> {
                    if (!Chat.getInstance().isInputOpened()) {
                        Chat.getInstance().openInput();
                    }
                }
                case KeyCode.ENTER -> {
                    if (e.isAlt()) {
                        if (MonitorManager.getInstance().isFullscreen()) {
                            MonitorManager.getInstance().setFullscreen(false);
                        } else {
                            long monitorDeviceId = MonitorManager.getInstance().getMonitorIdByWindow();
                            MonitorManager.getInstance().setMonitorDeviceId(monitorDeviceId);
                            MonitorManager.getInstance().setFullscreen(true);
                        }
                    }
                }
            }
        });

        addEventListener(this, InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.TAB -> {
                    Chat.getInstance().setVisible(true);
                    setTabWindowVisible(false);
                }

            }
        });

        worldScene = new WorldScene();
        add(worldScene);

        add(Chat.getInstance(), 10, 10);

        FpsMeter fpsMeter = new FpsMeter();
        add(fpsMeter, 10, 2);

        tabWindow = new TabWindow();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(this);

        addEventListener(this, Event.ADD_TO_STAGE, event -> {
            removeEventListener(this, Event.ADD_TO_STAGE);
            getStage().addEventListener(this, Event.RESIZE, resizeEvent -> {
                Chat.getInstance().setWidth(getStage().getWidth());
                Chat.getInstance().setHeight(getStage().getHeight() / 3);
                worldScene.resize(getStage().getWidth(), getStage().getHeight());
            });
        });

        INSTANCE = this;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverInfo(@NotNull ServerInfoDto dto) {
        tabWindow.setServerInfo(dto.getName(), dto.getPlayers().size(), dto.getMaxPlayers());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {
        Chat.getInstance().addMessage(text, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void rconResponse(@NotNull String rconResponseData) {
        rconResponseData.lines().forEach(Chat.getInstance()::addMessage);
    }

    @Override
    public void playerEnterServer(int id, @NotNull String name, int color) {
        D2D2WorldSound.playSoundAsset(PLAYER_ENTER);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerExit(@NotNull Player remotePlayer) {
        D2D2WorldSound.playSoundAsset(PLAYER_EXIT);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void localPlayerEnterServer(int localPlayerId,
                                       int localPlayerColor,
                                       @NotNull String serverProtocolVersion,
                                       @NotNull LocalDateTime serverStartTime) {

        Chat.getInstance().addMessage("Your id is " +
                        localPlayerId +
                        ", server protocol version is "
                        + serverProtocolVersion +
                        ". Server started at " + serverStartTime
                , Color.WHITE);
        worldScene.start();

        D2D2WorldArenaClientAssets.getAim().setColor(Color.WHITE);

        String rconPassword = CONFIG.getString(ClientConfig.RCON_PASSWORD);
        if (!rconPassword.isEmpty()) {
            CLIENT.sendRconLoginRequest(MD5.hash(rconPassword));
        }

        D2D2WorldSound.playSoundAsset(PLAYER_ENTER);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {
        Chat.getInstance().addMessage("Server: " + chatMessageText, Color.of(chatMessageTextColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerChat(int chatMessageId,
                           int playerId,
                           @NotNull String playerName,
                           int playerColor,
                           @NotNull String chatMessageText,
                           int textColor) {

        Chat.getInstance().addPlayerMessage(chatMessageId, playerId, playerName, playerColor, chatMessageText, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionClosed(@NotNull CloseStatus status) {
        connected = false;
        worldScene.stop();
        Chat.getInstance().addMessage(status.getErrorMessage(), Color.RED);
        new Lock().lock(5, SECONDS);

        if (connected) {
            attempts = 0;
            return;
        }

        if (attempts < 10) {
            start(server, CLIENT.getLocalPlayerName());
        } else {
            Chat.getInstance().addMessage("Can't establish connection. Please try again later");
            setBackgroundColor(Color.BLACK);
        }
        attempts++;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionEstablished() {
        Chat.getInstance().addMessage("Connection established", Color.GREEN);
        CLIENT.sendPlayerEnterRequest();
        connected = true;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerDeath(int deadPlayerId, int killerPlayerId) {
        Player deadPlayer = PLAYER_MANAGER.getPlayerById(deadPlayerId).orElseThrow();

        PLAYER_MANAGER.getPlayerById(killerPlayerId).ifPresentOrElse(killerPlayer -> {
                    Chat.getInstance().addMessage(
                            killerPlayer.getName() + "(" + killerPlayer.getId() + ") killed " +
                                    deadPlayer.getName() + "(" + deadPlayer.getId() + ")");

                    PLAYER_MANAGER.getPlayerById(killerPlayerId).orElseThrow().incrementFrags();
                },

                () -> {
                    Chat.getInstance().addMessage(
                            deadPlayer.getName() + "(" + deadPlayer.getId() + ") knocked out");

                    PLAYER_MANAGER.getPlayerById(deadPlayerId).orElseThrow().decrementFrags();
                });
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void fileReceiverProgress(@NotNull FileReceiver fileReceiver) {
        Chat.getInstance().addMessage(
                format("%d/%d content load %s",
                        fileReceiver.bytesLoaded(),
                        fileReceiver.bytesTotal(),
                        fileReceiver.getPath()),
                Color.DARK_GRAY);
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void fileReceiverComplete(@NotNull FileReceiver fileReceiver) {

    }

    private void setTabWindowVisible(boolean value) {
        tabWindow.removeFromParent();

        if (value) {
            tabWindow.setPlayers(PLAYER_MANAGER.getPlayerList());
            add(tabWindow);
        }
    }

    private boolean clientCommand(String text) {
        return COMMAND_PROCESSOR.process(text);
    }

    public void start(@NotNull String server, String localPlayerName) {
        log.debug("Staring... server: {}, player name: {}", server, localPlayerName);
        if (CLIENT.isConnected()) {
            CLIENT.close();
        }
        worldScene.init();

        this.server = server;

        String host = server.contains(":") ? server.split(":")[0] : server;
        int port = server.contains(":") ? Integer.parseInt(server.split(":")[1]) : DEFAULT_PORT;

        CLIENT.setLocalPlayerName(localPlayerName);

        Chat.getInstance().addMessage("Connecting to " + server + "...", Color.GRAY);
        CLIENT.connect(host, port);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void exit() {
        CLIENT.removeClientListener(this);
        CLIENT.sendExitRequest();
    }

}
