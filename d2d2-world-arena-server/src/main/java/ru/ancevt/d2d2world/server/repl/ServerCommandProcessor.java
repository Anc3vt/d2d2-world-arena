package ru.ancevt.d2d2world.server.repl;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.d2d2world.server.ServerTimer;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;
import ru.ancevt.d2d2world.server.service.GeneralService;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.repl.ReplInterpreter;
import ru.ancevt.util.texttable.TextTable;

import static ru.ancevt.d2d2world.server.ModuleContainer.modules;

@Slf4j
public class ServerCommandProcessor {

    private final ReplInterpreter repl;

    public ServerCommandProcessor() {
        repl = new ReplInterpreter();
        registerCommands();
    }

    public void execute(String commandText) {
        repl.execute(commandText);
    }

    private void registerCommands() {
        repl.addCommand("players", this::cmd_players);
        repl.addCommand("exit", this::cmd_exit);
        repl.addCommand("timer", this::cmd_timer);
    }

    private void cmd_timer(Args args) {
        modules.get(ServerTimer.class).setInterval(args.get(int.class, 0, 1));
    }

    private void cmd_exit(Args args) {
        modules.get(GeneralService.class).exit();
    }

    private void cmd_players(Args args) {
        TextTable table = new TextTable();
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping", "lastChatMsgId", "ctrlr", "x", "y"
        });

        modules.get(ServerPlayerManager.class).getPlayerList().forEach(p -> {
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
