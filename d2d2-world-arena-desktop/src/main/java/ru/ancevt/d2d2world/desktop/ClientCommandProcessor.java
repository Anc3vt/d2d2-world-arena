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
package ru.ancevt.d2d2world.desktop;

import ru.ancevt.commons.hash.MD5;
import ru.ancevt.d2d2world.desktop.scene.GameRoot;
import ru.ancevt.d2d2world.net.client.RemotePlayerManager;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.texttable.TextTable;

import static ru.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static ru.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;

public class ClientCommandProcessor {

    public static final ClientCommandProcessor MODULE_COMMAND_PROCESSOR = new ClientCommandProcessor();

    private ClientCommandProcessor() {
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
                var pm = RemotePlayerManager.PLAYER_MANAGER;
                TextTable tt = new TextTable();
                tt.setDecorEnabled(false);

                tt.setColumnNames(new String[]{"id", "name", "ping"});

                tt.addRow(
                        MODULE_CLIENT.getLocalPlayerId(),
                        MODULE_CLIENT.getLocalPlayerName(),
                        MODULE_CLIENT.getLocalPlayerPing(),
                        Integer.toString(MODULE_CLIENT.getLocalPlayerColor(), 16)
                );

                pm.getRemotePlayerList().forEach(
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

        return false;
    }
}
