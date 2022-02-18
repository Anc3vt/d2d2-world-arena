package ru.ancevt.d2d2world.server.repl;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.d2d2world.server.chat.Chat;
import ru.ancevt.d2d2world.server.player.PlayerManager;
import ru.ancevt.d2d2world.server.service.GeneralService;
import ru.ancevt.d2d2world.server.service.ServerSender;
import ru.ancevt.d2d2world.server.service.SyncService;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.repl.ReplInterpreter;
import ru.ancevt.util.texttable.TextTable;

@Slf4j
public class ServerRepl {


    private final GeneralService generalService;
    private final PlayerManager playerManager;
    private final ServerSender serverSender;
    private final Chat chat;
    private final SyncService syncService;
    private final ReplInterpreter replInterpreter;

    public ServerRepl(GeneralService generalService,
                      PlayerManager playerManager,
                      ServerSender serverSender,
                      Chat chat,
                      SyncService syncService) {

        this.generalService = generalService;
        this.playerManager = playerManager;
        this.serverSender = serverSender;
        this.chat = chat;
        this.syncService = syncService;

        replInterpreter = new ReplInterpreter();

        registerCommands();
    }

    private void registerCommands() {
        replInterpreter.addCommand("pm", this::cmd_pm);
    }

    private void cmd_pm(Args args) {
        TextTable table = new TextTable();
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping", "lastChatMsgId", "ctrlr", "x", "y"
        });

        playerManager.getPlayerList().forEach(p -> {
            table.addRow(
                    p.getId(),
                    p.hashCode(),
                    p.getName(),
                    Integer.toString(p.getColor(), 16),
                    p.getClientProtocolVersion(),
                    p.getAddress(),
                    p.getPingValue(),
                    p.getLastSeenChatMessageId(),
                    p.getControllerState(),
                    p.getX(),
                    p.getY()
            );
        });

        log.trace("\n{}", table.render());
    }

    public void start() {
        new Thread(replInterpreter::start, "repl").start();
    }


}
