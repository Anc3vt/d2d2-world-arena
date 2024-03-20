/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.client;

import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.client.net.PlayerManager;
import com.ancevt.d2d2world.client.storage.LocalStorageManager;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;
import com.ancevt.util.command.CommandSet;
import com.ancevt.util.command.NoSuchCommandException;
import com.ancevt.util.texttable.TextTable;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static com.ancevt.d2d2world.client.net.Client.CLIENT;

@Slf4j
public class ClientCommandProcessor {

    public static final ClientCommandProcessor COMMAND_PROCESSOR = new ClientCommandProcessor();

    private final CommandSet<Void> commandSet;
    private final Chat chat;

    private ClientCommandProcessor() {
        commandSet = new CommandSet<>();

        chat = Chat.getInstance();

        commandSet.registerCommand("/help", "print client command list", args -> {
            commandSet.getFormattedCommandList().lines().forEach(line -> Chat.getInstance().addMessage(line));
            return null;
        });

        commandSet.registerCommand("/q", "exit game", args -> {
            D2D2WorldArenaClientMain.exit();
            return null;
        });

        commandSet.registerCommand("/sound", "[on or off]", args -> {
            if (args.getElements().length >= 2) {
                SoundSystem.setEnabled(args.contains("on"));
            }
            return null;
        });

        commandSet.registerCommand("/getfile", "download file from server by path [path/to/file]", args -> {
            String path = args.get(String.class, 1);
            CLIENT.sendFileRequest(path);
            return null;
        });

        commandSet.registerCommand("/connection", "print info about current connection", args -> {
            chat.print(CLIENT.getConnection().toString());
            return null;
        });

        commandSet.registerCommand("/players", "print list of players", args -> {
            var pm = PlayerManager.PLAYER_MANAGER;
            TextTable tt = new TextTable();
            tt.setDecorEnabled(false);

            tt.setColumnNames(new String[]{"id", "name", "ping"});

            pm.getPlayerList().forEach(
                    p -> tt.addRow(p.getId(), p.getName(), p.getPing()));

            chat.print(tt.render());
            return null;
        });

        commandSet.registerCommand("/rcon", "send rcon command [/rcon command [params]]", args -> {
            // if the second (at index 1) token from command text is 'login'
            if ("login".equals(args.get(String.class, 1, ""))) {
                String passwordHash = MD5.hash(args.get(String.class, 2, ""));
                CLIENT.sendRconLoginRequest(passwordHash);
            } else {
                // send rcon command String beginning from 6 index
                CLIENT.sendRconCommand(args.getSource().substring(6));
            }
            return null;
        });

        LocalStorage ls = LocalStorageManager.localStorage();

        // LocalStorage section
        commandSet.registerCommand("/ls", "print local storage [group]", args -> {
            if (args.hasNext()) {
                chat.print(ls.toFormattedStringGroup(args.next(), true));
            } else {
                chat.print(ls.toFormattedString(true));
            }
            return null;
        });

        commandSet.registerCommand("/lsset", "set local storage item [key=value]", args -> {
            Args tokens = Args.of(args.next(), '=');
            String key = tokens.next();
            String value = tokens.hasNext() ? tokens.next() : "";
            chat.print(key + '=' + ls.getString(key), Color.GRAY);
            ls.put(key, value);
            chat.print(key + '=' + ls.getString(key), Color.GREEN);
            ls.save();
            return null;
        });

        commandSet.registerCommand("/lsrm", "remove item from local storage [item_key]", args -> {
            ls.remove(args.next());
            ls.save();
            return null;
        });

        commandSet.registerCommand("/lsrmg", "remove item group from local storage [group]", args -> {
            ls.removeGroup(args.next());
            ls.save();
            return null;
        });

        commandSet.registerCommand("/lsexport", "export data from local storage to file [file [group]]", args -> {
            String file = args.next();
            String group = args.hasNext() ? args.next() : null;
            if (group != null) {
                ls.exportGroupTo(Path.of(file), group);
            } else {
                ls.exportTo(Path.of(file));
            }
            return null;
        });

        commandSet.registerCommand("/lssave", "save local storage data", args -> {
            ls.save();
            return null;
        });

        commandSet.registerCommand("/lsload", "load local storage data", args -> {
            ls.load();
            return null;
        });

        commandSet.registerCommand("/lsclear", "clear local storage", args -> {
            ls.clear();
            return null;
        });

        commandSet.registerCommand("/lsdelete", "delete local storage resources on disk", args -> {
            ls.deleteResources();
            return null;
        });

        commandSet.registerCommand("/lsimport", "import data from file to local storage [file [[group]]", args -> {
            String file = args.next();
            String group = args.hasNext() ? args.next() : null;
            if (group != null) {
                ls.importGroupFrom(Path.of(file), group);
            } else {
                ls.importFrom(Path.of(file));
            }
            return null;
        });

        // End LocalStorage section
    }

    public CommandSet<Void> getCommandSet() {
        return commandSet;
    }

    public boolean process(String text) {
        try {
            chat.print(text, Color.YELLOW);
            commandSet.execute(text);
        } catch (NoSuchCommandException e) {
            chat.print(e.getMessage(), Color.RED);
        } catch (Exception e) {
            e.printStackTrace();
            chat.print(e.toString(), Color.RED);
        }
        return true;
    }

}
