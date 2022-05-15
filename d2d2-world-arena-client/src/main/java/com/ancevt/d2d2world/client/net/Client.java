
package com.ancevt.d2d2world.client.net;

import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.PlayerDto;
import com.ancevt.d2d2world.net.dto.client.PlayerActorRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto;
import com.ancevt.d2d2world.net.dto.client.PlayerEnterRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerEnterRoomStartDto;
import com.ancevt.d2d2world.net.dto.client.PlayerExitRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import com.ancevt.d2d2world.net.dto.client.PlayerTextToChatDto;
import com.ancevt.d2d2world.net.dto.client.RconCommandDto;
import com.ancevt.d2d2world.net.dto.client.RconLoginRequestDto;
import com.ancevt.d2d2world.net.dto.client.ServerInfoRequestDto;
import com.ancevt.d2d2world.net.dto.server.ChatDto;
import com.ancevt.d2d2world.net.dto.server.DeathDto;
import com.ancevt.d2d2world.net.dto.server.DestroyableBoxDestroyDto;
import com.ancevt.d2d2world.net.dto.server.MapContentInfoDto;
import com.ancevt.d2d2world.net.dto.server.PlayerActorDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterResponseDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterRoomStartResponseDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterServerDto;
import com.ancevt.d2d2world.net.dto.server.PlayerExitDto;
import com.ancevt.d2d2world.net.dto.server.RconResponseDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.dto.server.ServerTextDto;
import com.ancevt.d2d2world.net.dto.server.SetRoomDto;
import com.ancevt.d2d2world.net.dto.server.SpawnEffectDto;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImplListener;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;
import com.ancevt.d2d2world.sync.ISyncDataReceiver;
import com.ancevt.d2d2world.sync.SyncDataReceiver;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.TcpFactory;
import com.ancevt.net.connection.ConnectionListener;
import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2world.client.net.PlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.MODULE_CLIENT_PROTOCOL;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessageDamageReport;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessageFileRequest;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePing;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerAimXY;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerController;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerWeaponSwitch;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerXY;
import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.PROTOCOL_VERSION;
import static com.ancevt.d2d2world.net.transfer.Headers.HASH;
import static com.ancevt.d2d2world.net.transfer.Headers.PATH;
import static com.ancevt.d2d2world.net.transfer.Headers.newHeaders;

@Slf4j
public class Client implements ConnectionListener, ClientProtocolImplListener {

    public static final Client CLIENT = new Client();

    private IConnection connection;
    private final List<ClientListener> clientListeners;
    private ClientSender sender;
    private String serverProtocolVersion;
    private long pingRequestTime;
    private int localPlayerId;
    private String localPlayerName;
    private int localPlayerFrags;
    private int localPlayerColor;
    private final ISyncDataReceiver syncDataReceiver;
    private List<Integer> pingValues;

    private Client() {
        this.syncDataReceiver = new SyncDataReceiver();
        clientListeners = new CopyOnWriteArrayList<>();
        pingValues = new LinkedList<>();

        MODULE_CLIENT_PROTOCOL.addClientProtocolImplListener(this);
    }

    public ISyncDataReceiver getSyncDataReceiver() {
        return syncDataReceiver;
    }

    // CONNECTION LISTENER:

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionEstablished() {
        sendHandshake();
        clientListeners.forEach(ClientListener::clientConnectionEstablished);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionBytesReceived(byte[] bytes) {
        MODULE_CLIENT_PROTOCOL.bytesReceived(bytes);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionClosed(@NotNull CloseStatus status) {
        clientListeners.forEach(l -> l.clientConnectionClosed(status));
    }

    // CLIENT PROTOCOL LISTENER:

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void dtoFromServer(@NotNull Dto dto) {

        // TODO: extract to separate handler, maybe it should be a class
        if (dto instanceof MapContentInfoDto serverContentInfoDto) {

            Queue<String> queue = new ConcurrentLinkedDeque<>();

            queue.add(sendFileRequest("data/maps/" + serverContentInfoDto.getFilename()));

            serverContentInfoDto.getMapkits().forEach(
                    mk -> mk.getFiles().forEach(filename -> {
                                queue.add(sendFileRequest("data/mapkits/" + mk.getDirname() + "/" + filename));
                            }
                    ));
            FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {
                @Override
                public void fileReceiverProgress(FileReceiver fileReceiver) {

                }

                @Override
                public void fileReceiverComplete(FileReceiver fileReceiver) {
                    queue.remove(fileReceiver.getPath());

                    if (queue.isEmpty()) {
                        clientListeners.forEach(l -> l.mapContentLoaded(serverContentInfoDto.getFilename()));
                        FileReceiverManager.INSTANCE.removeFileReceiverManagerListener(this);
                    }
                }
            });

        } else if (dto instanceof PlayerActorDto d) {
            clientListeners.forEach(l -> l.localPlayerActorGameObjectId(d.getPlayerActorGameObjectId()));

        } else if (dto instanceof PlayerEnterServerDto d) {
            PlayerDto playerDto = d.getPlayer();
            PLAYER_MANAGER.getPlayerById(playerDto.getId()).ifPresentOrElse(remotePlayer -> {
                remotePlayer.update(playerDto.getName(), playerDto.getColor());
            }, () -> {
                PLAYER_MANAGER.addPlayer(playerDto.getId(), playerDto.getName(), playerDto.getColor());
            });
            clientListeners.forEach(l -> l.playerEnterServer(playerDto.getId(), playerDto.getName(), playerDto.getColor()));

        } else if (dto instanceof RconResponseDto d) {
            clientListeners.forEach(l -> l.rconResponse(d.getText()));

        } else if (dto instanceof PlayerEnterResponseDto d) {

            PlayerDto playerDto = d.getPlayer();
            setLocalPlayerId(playerDto.getId());
            setLocalPlayerColor(playerDto.getColor());
            setServerProtocolVersion(d.getProtocolVersion());
            clientListeners.forEach(l -> l.localPlayerEnterServer(localPlayerId, localPlayerColor, serverProtocolVersion, d.getServerStartTime()));

        } else if (dto instanceof PlayerExitDto d) {
            PLAYER_MANAGER.removePlayer(d.getPlayer().getId()).ifPresent(
                    remotePlayer -> clientListeners.forEach(l -> l.playerExit(remotePlayer))
            );

        } else if (dto instanceof ChatDto d) {
            d.getMessages().forEach(m -> {
                if (m.getPlayer() != null && m.getPlayer().getName() != null) {
                    PlayerDto p = m.getPlayer();
                    clientListeners.forEach(l -> l.playerChat(m.getId(), p.getId(), p.getName(), p.getColor(), m.getText(), m.getTextColor()));
                } else {
                    clientListeners.forEach(l -> l.serverChat(m.getId(), m.getText(), m.getTextColor()));
                }
            });

        } else if (dto instanceof ServerInfoDto d) {
            Set<PlayerDto> playerDtoSet = d.getPlayers();
            playerDtoSet.forEach(p -> {
                Player player = PLAYER_MANAGER.addPlayer(p.getId(), p.getName(), p.getColor());
                player.setPing(p.getPing());
                player.setFrags(p.getFrags());
            });

            clientListeners.forEach(l -> l.serverInfo(d));

        } else if (dto instanceof ServerTextDto d) {
            clientListeners.forEach(l -> l.serverTextToPlayer(d.getText(), d.getColor()));

        } else if (dto instanceof DeathDto d) {
            clientListeners.forEach(l -> l.playerDeath(d.getDeadPlayerId(), d.getKillerPlayerId()));

        } else if (dto instanceof PlayerChatEventDto d) {
            int playerId = d.getPlayerId();
            String action = d.getAction();
            PLAYER_MANAGER.getPlayerById(playerId).ifPresent(
                    player -> player.setChatOpened(PlayerChatEventDto.OPEN.equals(action))
            );
            clientListeners.forEach(l -> l.playerChatEvent(playerId, action));

        } else if (dto instanceof PlayerEnterRoomStartResponseDto d) {
            clientListeners.forEach(l -> l.playerEnterRoomStartResponseReceived());
        } else if (dto instanceof SetRoomDto d) {
            clientListeners.forEach(l -> l.setRoom(d.getRoomId(), d.getCameraX(), d.getCameraY()));
        } else if (dto instanceof SpawnEffectDto d) {
            clientListeners.forEach(l -> l.spawnEffect(d.getX(), d.getY()));
        } else if (dto instanceof DestroyableBoxDestroyDto d) {
            clientListeners.forEach(l -> l.destroyableBoxDestroy(d.getDestroyableGameObjectId()));
        }
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerPingResponse() {
        long pingResponseTime = System.currentTimeMillis();
        var pingValue = (int) (pingResponseTime - pingRequestTime) / 2; // devide by 2 because that is average of sending and receiving time values
        pingValues.add(pingValue);
        sender.send(PlayerPingReportDto.builder().ping(pingValue).build());
        PLAYER_MANAGER.getPlayerById(localPlayerId).ifPresent(player -> {
            int pingAverage = getAveragePing();
            player.setPing(pingAverage);
            sender.send(PlayerPingReportDto.builder().ping(pingAverage).build());
        });
    }

    private int getAveragePing() {
        int sum = 0;
        for (int p : pingValues) {
            sum += p;
        }
        int result = sum / pingValues.size();

        if (pingValues.size() > 10) {
            pingValues = pingValues.subList(10 / 2, 10 - 1);
        }

        return result;
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {
        clientListeners.forEach(l -> l.fileData(headers, fileData));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverSyncData(byte @NotNull [] syncData) {
        syncDataReceiver.bytesReceived(syncData);
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerShoot(int playerId) {
        clientListeners.forEach(l -> l.playerShoot(playerId));
    }

    // SENDERS:

    public void sendHandshake() {
        sender.send(new byte[]{(byte) MessageType.HANDSHAKE});
    }

    public void sendPlayerEnterRequest() {
        sender.send(PlayerEnterRequestDto.builder()
                .name(localPlayerName)
                .protocolVersion(PROTOCOL_VERSION)
                .build()
        );
    }

    public void sendPlayerEnterRoom(String roomId, float x, float y) {
        sender.send(PlayerEnterRoomStartDto.builder()
                .roomId(roomId)
                .x(x)
                .y(y)
                .build());
    }

    public void sendPlayerActorRequest() {
        sender.send(PlayerActorRequestDto.builder().build());
    }

    public void sendHook(int hookGameObjectId) {
        sender.send(ClientProtocolImpl.createMessageHook(hookGameObjectId));
    }

    public void sendDamageReport(int damageValue, int damagingGameObjectId) {
        sender.send(createMessageDamageReport(damageValue, damagingGameObjectId));
    }

    public void sendAimXY(float aimX, float aimY) {
        sender.send(createMessagePlayerAimXY(aimX, aimY));
    }

    public void sendXY(float x, float y) {
        sender.send(createMessagePlayerXY(x, y));
    }

    public void sendLocalPlayerWeaponSwitch(int delta) {
        sender.send(createMessagePlayerWeaponSwitch(delta));
    }

    public void sendLocalPlayerController(int controllerState) {
        sender.send(createMessagePlayerController(controllerState));
    }

    public void sendServerInfoRequest() {
        sender.send(ServerInfoRequestDto.INSTANCE);
    }

    public void sendPingRequest() {
        pingRequestTime = System.currentTimeMillis();
        sender.send(createMessagePing());
    }

    public String sendFileRequest(@NotNull String path) {
        var h = newHeaders();

        if (FileSystemUtils.exists(path)) {
            h.put(HASH, MD5.hashFile(path));
        }

        return sendFileRequest(h.put(PATH, path));
    }

    public String sendFileRequest(@NotNull Headers headers) {
        sender.send(createMessageFileRequest(headers.toString()));
        return headers.get(PATH);
    }

    public void sendDto(Dto dto) {
        sender.send(dto);
    }

    public void sendChatMessage(String text) {
        sender.send(PlayerTextToChatDto.builder()
                .text(text)
                .build()
        );
    }

    public void sendExitRequest() {
        sender.send(PlayerExitRequestDto.INSTANCE);
    }

    public void sendRconLoginRequest(String passwordHash) {
        sender.send(RconLoginRequestDto.builder()
                .passwordHash(passwordHash)
                .build()
        );
    }

    public void sendRconCommand(String rconCommandText) {
        sender.send(RconCommandDto.builder()
                .commandText(rconCommandText)
                .build()
        );
    }

    ///

    public boolean isEnteredServer() {
        return localPlayerName != null;
    }

    public void connect(String host, int port) {
        if (connection != null) {
            connection.closeIfOpen();
            connection.removeConnectionListener(this);
        }

        connection = TcpFactory.createConnection(0);
        log.info("connecting... Connection object: {}", connection);
        sender = new ClientSender(connection);
        connection.addConnectionListener(this);
        connection.asyncConnect(host, port);
    }

    public void addClientListener(ClientListener l) {
        clientListeners.add(l);
    }

    public void removeClientListener(ClientListener l) {
        clientListeners.remove(l);
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerName(@NotNull String playerName) {
        this.localPlayerName = playerName;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = playerId;
    }

    public int getLocalPlayerColor() {
        return localPlayerColor;
    }

    public void setLocalPlayerColor(int color) {
        this.localPlayerColor = color;
    }

    public int getLocalPlayerPing() {
        // improve .get if error occurred
        return PLAYER_MANAGER.getPlayerById(localPlayerId).get().getPing();
    }

    public void setLocalPlayerFrags(int localPlayerFrags) {
        this.localPlayerFrags = localPlayerFrags;
    }

    public int getLocalPlayerFrags() {
        return localPlayerFrags;
    }

    public void close() {
        connection.close();
    }

    public void setServerProtocolVersion(@NotNull String serverProtocolVersion) {
        this.serverProtocolVersion = serverProtocolVersion;
    }

    public String getServerProtocolVersion() {
        return serverProtocolVersion;
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    public IConnection getConnection() {
        return connection;
    }

}


