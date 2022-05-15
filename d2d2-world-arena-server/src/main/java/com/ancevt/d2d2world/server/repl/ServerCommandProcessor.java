
package com.ancevt.d2d2world.server.repl;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.World;
import com.ancevt.net.connection.IConnection;
import com.ancevt.util.args.Args;
import com.ancevt.util.command.CommandRepl;
import com.ancevt.util.command.CommandSet;
import com.ancevt.util.command.NoSuchCommandException;
import com.ancevt.util.texttable.TextTable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;
import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.BanList.BANLIST;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.service.GeneralService.MODULE_GENERAL;
import static com.ancevt.d2d2world.server.service.ServerUnit.MODULE_SERVER_UNIT;
import static com.ancevt.d2d2world.server.scene.ServerWorldScene.SERVER_WORLD_SCENE;

@Slf4j
public class ServerCommandProcessor {

    public static final ServerCommandProcessor MODULE_COMMAND_PROCESSOR = new ServerCommandProcessor();

    private final CommandRepl commandRepl;
    private final CommandSet<String> commandSet;

    private ServerCommandProcessor() {
        commandSet = new CommandSet<>();
        commandRepl = new CommandRepl(commandSet);
        registerCommands();
    }

    public @NotNull String execute(String commandText) {
        try {
            return commandSet.execute(commandText);
        } catch (NoSuchCommandException e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    private void registerCommands() {
        commandSet.registerCommand("help", "print this command list", this::cmd_help);
        commandSet.registerCommand("players", "print player list", this::cmd_players);
        commandSet.registerCommand("exit", "stop server", this::cmd_exit);
        commandSet.registerCommand("syncdir", "send directory to player from server filesystem", this::cmd_syncdir);
        commandSet.registerCommand("syncmap", "send map to player from server filesystem", this::cmd_syncmap);
        commandSet.registerCommand("syncmapkit", "send mapkit to player from server filesystem", this::cmd_syncmapkit);
        commandSet.registerCommand("mapkits", "print server mapkit list", this::cmd_mapkits);
        commandSet.registerCommand("maps", "print server map list", this::cmd_maps);
        commandSet.registerCommand("setmap", "start map by name", this::cmd_setmap);
        commandSet.registerCommand("banlist", "print ban list", this::cmd_banlist);
        commandSet.registerCommand("ban", "ban player by id or by ip", this::cmd_ban);
        commandSet.registerCommand("unban", "remove ip from ban list by player id or ip", this::cmd_unban);
        commandSet.registerCommand("fps", "print server FPS", this::cmd_fps);
        commandSet.registerCommand("tostring", "print game object info by id", this::cmd_tostring);
        commandSet.registerCommand("kill", "kill player by id", this::cmd_kill);
        commandSet.registerCommand("switchroom", "instant switch room for player by player id [playerId \"roomId\" x y]", this::cmd_switchroom);
        commandSet.registerCommand("setprop", "set property for game object id [gid key val]", this::cmd_setprop);
        commandSet.registerCommand("connections", "print active connection list", this::cmd_connections);
        commandSet.registerCommand("reset", "reset all map", this::cmd_reset);
    }

    private @NotNull String cmd_reset(@NotNull Args args) {
        SERVER_WORLD_SCENE.resetAllResettableGameObjects();
        return "";
    }

    private @NotNull String cmd_connections(@NotNull Args args) {
        StringBuilder stringBuilder = new StringBuilder();
        MODULE_SERVER_UNIT.server.getConnections().forEach(connection -> stringBuilder.append(connection).append('\n'));
        return stringBuilder.toString();
    }

    private @NotNull String cmd_setprop(@NotNull Args args) {
        int gameObjectId = args.get(int.class, 1, 0);
        IGameObject gameObject = SERVER_WORLD_SCENE.getWorldByGameObjectId(gameObjectId).getGameObjectById(gameObjectId);
        String prop = args.get(String.class, 2) + "=" + args.get(String.class, 3);
        setProperties(gameObject, DataEntry.newInstance(prop));
        return getProperties(gameObject).toString();
    }

    private @NotNull String cmd_switchroom(@NotNull Args args) {
        int playerId = args.get(int.class, 1);
        String roomId = args.get(String.class, 2);
        float x = args.get(float.class, 3, 64f);
        float y = args.get(float.class, 4, 64f);
        SERVER_WORLD_SCENE.instantSwitchRoomForPlayerActor(playerId, roomId, x, y);
        return "";
    }

    private @NotNull String cmd_kill(@NotNull Args args) {
        SERVER_WORLD_SCENE.getPlayerActorByPlayerId(args.get(int.class, 1, 0)).ifPresent(playerActor -> playerActor.setHealth(0));
        return "";
    }

    private @NotNull String cmd_tostring(@NotNull Args args) {
        int gameObjectId = args.get(int.class, 1, 0);
        if (gameObjectId == 0) return null;

        World world = SERVER_WORLD_SCENE.getWorldByGameObjectId(gameObjectId);
        if (world == null) {
            return "no such game object, id: " + gameObjectId;
        }

        IGameObject gameObject = world.getGameObjectById(gameObjectId);
        return world.toString() + "\n--\n"
                + gameObject.toString() + "\n--\n"
                + getProperties(gameObject) + "\n--\n"
                + gameObject.getMapkitItem().getDataEntry();
    }

    private String cmd_fps(@NotNull Args args) {
        try {
            int fps = SERVER_WORLD_SCENE.getFps();
            System.out.println(fps);
            return String.valueOf(fps);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String cmd_unban(@NotNull Args args) {
        try {
            String a = args.get(String.class, 1);
            String ip = a.contains(".") ? a : IConnection.getIpFromAddress(MODULE_GENERAL.getConnection(
                    args.get(int.class, 1)).orElseThrow().getRemoteAddress());
            BANLIST.unban(ip);
            return "Unbanned ip " + ip;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull String cmd_ban(@NotNull Args args) {
        try {
            String a = args.get(String.class, 1);
            String ip = a.contains(".") ? a : IConnection.getIpFromAddress(MODULE_GENERAL.getConnection(args.get(int.class, 1)).orElseThrow().getRemoteAddress());
            BANLIST.ban(ip);
            String result = "Banned ip " + ip;
            MODULE_SERVER_UNIT.server.getConnections().stream().filter(c -> {
                        String connectionIp = IConnection.getIpFromAddress(c.getRemoteAddress());
                        return connectionIp.equals(ip);
                    })
                    .findAny()
                    .orElseThrow()
                    .closeIfOpen();
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull String cmd_banlist(@NotNull Args args) {
        StringBuilder s = new StringBuilder();
        BANLIST.getList().forEach(ip -> s.append(ip).append('\n'));
        return s.toString();
    }

    private String cmd_setmap(@NotNull Args args) {
        try {
            String mapName = args.get(String.class, 1);
            MODULE_GENERAL.setMap(mapName);
            return "set map '" + mapName + "'";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    private @NotNull String cmd_syncmapkit(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 1);
            String mapkitName = args.get(String.class, 2);
            MODULE_CONTENT_MANAGER.syncSendMapkit(mapkitName, playerId);
            return "sync mapkit '" + mapkitName + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull String cmd_syncmap(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 1);
            String mapName = args.get(String.class, 2);
            MODULE_CONTENT_MANAGER.syncSendMap(mapName, playerId);
            return "sync map '" + mapName + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull String cmd_maps(@NotNull Args args) {
        StringBuilder s = new StringBuilder();
        MODULE_CONTENT_MANAGER.getMaps().forEach(mapkit -> s.append(mapkit.toString()).append('\n'));
        System.out.println(s);
        return s.toString();
    }

    @Contract(pure = true)
    private @NotNull String cmd_mapkits(@NotNull Args args) {
        StringBuilder s = new StringBuilder();
        MODULE_CONTENT_MANAGER.getMapkits().forEach(mapkit -> s.append(mapkit.toString()).append('\n'));
        System.out.println(s);
        return s.toString();
    }

    private String cmd_help(@NotNull Args args) {
        return commandSet.getFormattedCommandList();
    }

    private @NotNull String cmd_syncdir(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 1);
            String path = args.get(String.class, 2);
            MODULE_CONTENT_MANAGER.syncSendDirectoryToPlayer(path, playerId);
            return "sync path '" + path + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @Nullable String cmd_exit(@NotNull Args args) {
        MODULE_GENERAL.exit();
        return null;
    }

    private String cmd_players(@NotNull Args args) {
        TextTable table = new TextTable();
        table.setDecorEnabled(false);
        table.setColumnNames(new String[]{
                "id", "hash", "name", "color", "clntProtVer", "address", "ping", "room"
        });

        PLAYER_MANAGER.getPlayerList().forEach(p -> {
            table.addRow(
                    p.getId(),
                    p.hashCode(),
                    p.getName(),
                    Integer.toString(p.getColor(), 16),
                    p.getClientProtocolVersion(),
                    p.getAddress(),
                    p.getPingValue(),
                    p.getRoomId()
            );
        });

        String result = table.render();
        System.out.println(result);
        return result;
    }

    public void start() {
        Async.run(() -> {
            try {
                commandRepl.start(System.in, System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
