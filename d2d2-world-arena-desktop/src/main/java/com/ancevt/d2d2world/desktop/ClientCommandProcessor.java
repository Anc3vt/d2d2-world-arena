/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.desktop;

import com.ancevt.commons.Holder;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2world.desktop.scene.GameRoot;
import com.ancevt.d2d2world.net.client.PlayerManager;
import com.ancevt.util.args.Args;
import com.ancevt.util.texttable.TextTable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static com.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;

public class ClientCommandProcessor {

    public static final ClientCommandProcessor MODULE_COMMAND_PROCESSOR = new ClientCommandProcessor();

    private final Set<Command> commands;

    private ClientCommandProcessor() {
        commands = new HashSet<>();
    }

    public Set<Command> getCommands() {
        return commands;
    }

    public boolean process(String text) {
        Args tokens = new Args(text);

        String command = tokens.get(String.class, 0);

        switch (command) {
            case "/exit", "/q", "/quit" -> {
                MODULE_CLIENT.sendExitRequest();
                GameRoot.INSTANCE.exit();
            }

            case "//getfile" -> {
                String path = tokens.get(String.class, 1);
                MODULE_CLIENT.sendFileRequest(path);
                return true;
            }

            case "//connection" -> {
                MODULE_CHAT.addMessage(MODULE_CLIENT.getConnection().toString());
                return true;
            }

            case "//players" -> {
                var pm = PlayerManager.PLAYER_MANAGER;
                TextTable tt = new TextTable();
                tt.setDecorEnabled(false);

                tt.setColumnNames(new String[]{"id", "name", "ping"});

                pm.getPlayerList().forEach(
                        p -> tt.addRow(p.getId(), p.getName(), p.getPing()));

                MODULE_CHAT.addMessage(tt.render());
                return true;
            }

            case "/rcon" -> {
                // if the second (at index 1) token from command text is 'login'
                if ("login".equals(tokens.get(String.class, 1, ""))) {
                    String passwordHash = MD5.hash(tokens.get(String.class, 2, ""));
                    MODULE_CLIENT.sendRconLoginRequest(passwordHash);
                } else {
                    // send rcon command String beginning from 6 index
                    MODULE_CLIENT.sendRconCommand(text.substring(6));
                }
                return true;
            }
        }

        Holder<Boolean> result = new Holder<>(false);
        commands.stream()
                .filter(c->c.command.equals(command))
                .findAny()
                .ifPresent(c->result.setValue(c.function().apply(tokens)));

        return result.getValue();
    }

    public record Command(String command, Function<Args, Boolean> function) {
    }
}
