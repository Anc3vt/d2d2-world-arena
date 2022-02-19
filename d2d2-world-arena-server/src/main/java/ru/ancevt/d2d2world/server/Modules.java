package ru.ancevt.d2d2world.server;

import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.server.chat.ServerChat;
import ru.ancevt.d2d2world.server.player.PlayerManager;
import ru.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import ru.ancevt.d2d2world.server.service.GeneralService;
import ru.ancevt.d2d2world.server.service.ServerSender;
import ru.ancevt.d2d2world.server.service.SyncService;
import ru.ancevt.net.messaging.server.IServer;
import ru.ancevt.net.messaging.server.ServerFactory;

public class Modules {
    public static final Config CONFIG = Config.INSTANCE;
    public static final PlayerManager PLAYER_MANAGER = PlayerManager.INSTANCE;
    public static final SyncService SYNC_SERVICE = SyncService.INSTANCE;
    public static final ServerChat SERVER_CHAT = ServerChat.INSTANCE;
    public static final ServerSender SENDER = ServerSender.INSTANCE;
    public static final ServerInfo SERVER_INFO = ServerInfo.INSTANCE;
    public static final IServer SERVER_UNIT = ServerFactory.createTcpB254Server();
    public static final ServerProtocolImpl SERVER_PROTOCOL_IMPL = new ServerProtocolImpl();
    public static final ServerTimer TIMER = ServerTimer.INSTANCE;
    public static final ServerCommandProcessor COMMAND_PROCESSOR = ServerCommandProcessor.INSTANCE;
    public static final GeneralService GENERAL_SERVICE = GeneralService.INSTANCE;
}
