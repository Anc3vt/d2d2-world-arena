/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.desktop;

import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.desktop.ui.chat.Chat;
import com.ancevt.d2d2world.net.client.PlayerManager;
import com.ancevt.util.args.Args;
import com.ancevt.util.command.CommandSet;
import com.ancevt.util.command.NoSuchCommandException;
import com.ancevt.util.texttable.TextTable;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

import static com.ancevt.d2d2world.net.client.Client.CLIENT;

@Slf4j
public class ClientCommandProcessor {

    public static final ClientCommandProcessor COMMAND_PROCESSOR = new ClientCommandProcessor();

    private final CommandSet<Void> commandSet;

    private ClientCommandProcessor() {
        commandSet = new CommandSet<>();

        commandSet.registerCommand(
                "//help",
                "print client command list",
                args -> {
                    commandSet.getFormattedCommandList().lines().forEach(line-> Chat.getInstance().addMessage(line));
                    return null;
                }
        );

        commandSet.registerCommand(
                "/q",
                "exit game",
                args -> {
                    D2D2WorldArenaDesktopMain.exit();
                    return null;
                }
        );

        commandSet.registerCommand(
                "//sound",
                "[on or off]",
                args -> {
                    if (args.getElements().length >= 2) {
                        SoundSystem.setEnabled(args.contains("on"));
                    }
                    return null;
                }
        );

        commandSet.registerCommand(
                "//getfile",
                "download file from server by path [path/to/file]",
                args -> {
                    String path = args.get(String.class, 1);
                    CLIENT.sendFileRequest(path);
                    return null;
                }
        );

        commandSet.registerCommand(
                "//connection",
                "print info about current connection",
                args -> {
                    Chat.getInstance().addMessage(CLIENT.getConnection().toString());
                    return null;
                }
        );

        commandSet.registerCommand(
                "//players",
                "print list of players",
                args -> {
                    var pm = PlayerManager.PLAYER_MANAGER;
                    TextTable tt = new TextTable();
                    tt.setDecorEnabled(false);

                    tt.setColumnNames(new String[]{"id", "name", "ping"});

                    pm.getPlayerList().forEach(
                            p -> tt.addRow(p.getId(), p.getName(), p.getPing()));

                    Chat.getInstance().addMessage(tt.render());
                    return null;
                }
        );

        commandSet.registerCommand(
                "/rcon",
                "send rcon command [/rcon command [params]]",
                args -> {
                    // if the second (at index 1) token from command text is 'login'
                    if ("login".equals(args.get(String.class, 1, ""))) {
                        String passwordHash = MD5.hash(args.get(String.class, 2, ""));
                        CLIENT.sendRconLoginRequest(passwordHash);
                    } else {
                        // send rcon command String beginning from 6 index
                        CLIENT.sendRconCommand(args.getSource().substring(6));
                    }
                    return null;
                }
        );
    }

    public CommandSet<Void> getCommandSet() {
        return commandSet;
    }

    public boolean process(String text) {
        try {
            commandSet.execute(text);
        } catch (NoSuchCommandException e) {
            Chat.getInstance().addMessage(e.getMessage());
        }
        return true;
    }

    public record Command(String command, Function<Args, Boolean> function) {
    }
}
