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
import ru.ancevt.d2d2world.desktop.ui.chat.Chat;
import ru.ancevt.d2d2world.net.client.Client;
import ru.ancevt.d2d2world.net.client.RemotePlayerManager;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.texttable.TextTable;

public class ClientCommandProcessor {

    public static final ClientCommandProcessor INSTANCE = new ClientCommandProcessor();

    private ClientCommandProcessor() {

    }


    public boolean process(String text) {
        Args args = new Args(text);

        String command = args.get(String.class, 0);

        switch (command) {
            case "/exit", "/q", "/quit" -> {
                Client.INSTANCE.sendExitRequest();

                // TODO: improve this crack
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);

            }

            case "//players" -> {
                var pm = RemotePlayerManager.INSTANCE;
                TextTable tt = new TextTable();
                tt.setDecorEnabled(false);

                tt.setColumnNames(new String[]{"id", "name", "ping"});

                tt.addRow(
                        Client.INSTANCE.getLocalPlayerId(),
                        Client.INSTANCE.getLocalPlayerName(),
                        Client.INSTANCE.getPing(),
                        Integer.toString(Client.INSTANCE.getLocalPlayerColor(), 16)
                );

                pm.getRemotePlayerList().forEach(
                        p -> tt.addRow(p.getId(), p.getName(), p.getPing()));

                String[] lines = tt.render().split("\n");
                for (String line : lines) {
                    Chat.INSTANCE.addMessage(line);
                }
                return true;
            }

            case "/rcon" -> {
                // if the second (at index 1) token from command text is 'login'
                if("login".equals(args.get(String.class, 1, ""))) {
                    String passwordHash = MD5.hash(args.get(String.class, 2, ""));
                    Client.INSTANCE.sendRconLoginRequest(passwordHash);
                } else {
                    Client.INSTANCE.sendRconCommand(text.substring(6));
                }

            }
        }

        return false;
    }
}
