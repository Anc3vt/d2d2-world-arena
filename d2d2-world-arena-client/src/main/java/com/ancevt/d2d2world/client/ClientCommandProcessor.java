
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
            Chat.getInstance().addMessage(e.getMessage(), Color.RED);
        }
        return true;
    }

}
