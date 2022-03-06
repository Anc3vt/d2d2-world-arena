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
import ru.ancevt.util.args.Args;
import ru.ancevt.util.repl.ReplInterpreter;
import ru.ancevt.util.texttable.TextTable;

import static ru.ancevt.d2d2world.server.ServerTimer.MODULE_TIMER;
import static ru.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static ru.ancevt.d2d2world.server.player.ServerPlayerManager.MODULE_PLAYER_MANAGER;
import static ru.ancevt.d2d2world.server.service.GeneralService.MODULE_GENERAL;

@Slf4j
public class ServerCommandProcessor {

    public static final ServerCommandProcessor MODULE_COMMAND_PROCESSOR = new ServerCommandProcessor();

    private final ReplInterpreter repl;

    private ServerCommandProcessor() {
        repl = new ReplInterpreter();
        registerCommands();
    }

    public @NotNull String execute(String commandText) {
        return String.valueOf(repl.execute(commandText));
    }

    private void registerCommands() {
        repl.addCommand("help", this::cmd_help);
        repl.addCommand("players", this::cmd_players);
        repl.addCommand("exit", this::cmd_exit);
        repl.addCommand("loopdelay", this::cmd_loopdelay);
        repl.addCommand("syncdir", this::cmd_syncdir);
        repl.addCommand("mapkits", this::cmd_mapkits);
        repl.addCommand("maps", this::cmd_maps);
    }

    private @NotNull @Unmodifiable Object cmd_maps(Args args) {
        StringBuilder s = new StringBuilder();
        MODULE_CONTENT_MANAGER.getMaps().forEach(mapkit -> s.append(mapkit.toString()).append('\n'));
        System.out.println(s);
        return s.toString();
    }

    @Contract(pure = true)
    private @NotNull @Unmodifiable Object cmd_mapkits(Args args) {
        StringBuilder s = new StringBuilder();
        MODULE_CONTENT_MANAGER.getMapkits().forEach(mapkit -> s.append(mapkit.toString()).append('\n'));
        System.out.println(s);
        return s.toString();
    }

    @Contract(pure = true)
    private @NotNull @Unmodifiable Object cmd_help(Args args) {
        StringBuilder s = new StringBuilder();
        repl.getCommands().forEach(c -> s.append(c.getCommandWord()).append('\n'));
        return s.toString();
    }

    private @NotNull Object cmd_syncdir(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 0);

            String path = args.get(String.class, 1);
            MODULE_CONTENT_MANAGER.syncSendDirectoryToPlayer(path, playerId);
            return "sync path " + path;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull @Unmodifiable Object cmd_loopdelay(@NotNull Args args) {
        MODULE_TIMER.setInterval(args.get(int.class, 0, 1));
        return String.valueOf(MODULE_TIMER.getInterval());
    }

    private @Nullable Object cmd_exit(Args args) {
        MODULE_GENERAL.exit();
        return null;
    }

    private @Unmodifiable Object cmd_players(Args args) {
        TextTable table = new TextTable();
        table.setDecorEnabled(false);
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping"
        });

        MODULE_PLAYER_MANAGER.getPlayerList().forEach(p -> {
            table.addRow(
                    p.getId(),
                    p.hashCode(),
                    p.getName(),
                    Integer.toString(p.getColor(), 16),
                    p.getClientProtocolVersion(),
                    p.getAddress(),
                    p.getPingValue(),
                    p.getLastSeenChatMessageId()
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
