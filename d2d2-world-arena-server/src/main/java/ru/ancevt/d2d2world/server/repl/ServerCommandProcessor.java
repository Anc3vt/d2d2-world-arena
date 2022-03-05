/*
 *   D2D2 World Arena Server
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
package ru.ancevt.d2d2world.server.repl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import ru.ancevt.d2d2world.server.ServerTimer;
import ru.ancevt.d2d2world.server.content.ContentManager;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;
import ru.ancevt.d2d2world.server.service.GeneralService;
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

    public @NotNull String execute(String commandText) {
        return String.valueOf(repl.execute(commandText));
    }

    private void registerCommands() {
        repl.addCommand("players", this::cmd_players);
        repl.addCommand("exit", this::cmd_exit);
        repl.addCommand("loopdelay", this::cmd_loopdelay);
        repl.addCommand("syncdir", this::cmd_syncdir);
        repl.addCommand("help", this::cmd_help);

    }

    @Contract(pure = true)
    private @NotNull Object cmd_help(Args args) {
        StringBuilder s = new StringBuilder();
        repl.getCommands().forEach(c -> s.append(c.getCommandWord()).append('\n'));
        return s.toString();
    }

    private @NotNull Object cmd_syncdir(@NotNull Args args) {
        int playerId = args.get(int.class, 0);
        String path = args.get(String.class, 1);
        ContentManager.INSTANCE.syncSendDirectoryToPlayer(path, playerId);
        return "sync path " + path;
    }

    private @NotNull @Unmodifiable Object cmd_loopdelay(@NotNull Args args) {
        ServerTimer.INSTANCE.setInterval(args.get(int.class, 0, 1));
        return String.valueOf(ServerTimer.INSTANCE.getInterval());
    }

    private @Nullable Object cmd_exit(Args args) {
        GeneralService.INSTANCE.exit();
        return null;
    }

    private @Unmodifiable Object cmd_players(Args args) {
        TextTable table = new TextTable();
        table.setDecorEnabled(false);
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping", "lastChatMsgId", "ctrlr", "x", "y"
        });

        ServerPlayerManager.INSTANCE.getPlayerList().forEach(p -> {
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

        String result = table.render();
        System.out.println(result);
        return result;
    }

    public void start() {
        new Thread(repl::start, "repl").start();
    }
}
