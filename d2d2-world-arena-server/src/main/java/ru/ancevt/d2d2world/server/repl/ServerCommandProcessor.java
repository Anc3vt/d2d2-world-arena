package ru.ancevt.d2d2world.server.repl;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.d2d2world.server.D2D2WorldServer;
import ru.ancevt.d2d2world.server.Modules;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.repl.ReplInterpreter;
import ru.ancevt.util.texttable.TextTable;

@Slf4j
public class ServerCommandProcessor {

    public static final ServerCommandProcessor INSTANCE = new ServerCommandProcessor();


    private final ReplInterpreter repl;

    private ServerCommandProcessor() {
        repl = new ReplInterpreter();
        registerCommands();
    }

    public void execute(String commandText) {
        repl.execute(commandText);
    }

    private void registerCommands() {
        repl.addCommand("players", this::cmd_players);
        repl.addCommand("exit", this::cmd_exit);
    }

    private void cmd_exit(Args args) {
        Modules.GENERAL_SERVICE.exit();
    }

    private void cmd_players(Args args) {
        TextTable table = new TextTable();
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping", "lastChatMsgId", "ctrlr", "x", "y"
        });

        Modules.PLAYER_MANAGER.getPlayerList().forEach(p -> {
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
        new Thread(repl::start, "repl").start();
    }


}
