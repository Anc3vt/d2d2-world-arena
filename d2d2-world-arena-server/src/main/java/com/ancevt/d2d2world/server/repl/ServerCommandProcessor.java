/*
 *   D2D2 World Arena Server
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
package com.ancevt.d2d2world.server.repl;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.World;
import com.ancevt.net.connection.IConnection;
import com.ancevt.util.args.Args;
import com.ancevt.util.repl.ReplInterpreter;
import com.ancevt.util.texttable.TextTable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;
import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.BanList.BANLIST;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.service.GeneralService.MODULE_GENERAL;
import static com.ancevt.d2d2world.server.service.ServerUnit.MODULE_SERVER_UNIT;
import static com.ancevt.d2d2world.server.simulation.ServerWorldScene.WORLD_SCENE;

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
        repl.addCommand("syncdir", this::cmd_syncdir);
        repl.addCommand("syncmap", this::cmd_syncmap);
        repl.addCommand("syncmapkit", this::cmd_syncmapkit);
        repl.addCommand("mapkits", this::cmd_mapkits);
        repl.addCommand("maps", this::cmd_maps);
        repl.addCommand("setmap", this::cmd_setmap);
        repl.addCommand("banlist", this::cmd_banlist);
        repl.addCommand("ban", this::cmd_ban);
        repl.addCommand("unban", this::cmd_unban);
        repl.addCommand("fps", this::cmd_fps);
        repl.addCommand("tostring", this::cmd_tostring);
        repl.addCommand("kill", this::cmd_kill);
        repl.addCommand("tw", this::cmd_tw);
        repl.addCommand("setprop", this::cmd_setprop);
        repl.addCommand("connections", this::cmd_connections);
        repl.addCommand("reset", this::cmd_reset);
    }

    private Object cmd_reset(Args args) {
        WORLD_SCENE.resetAllResettableGameObjects();
        return "";
    }

    private @NotNull @Unmodifiable Object cmd_connections(Args args) {
        StringBuilder stringBuilder = new StringBuilder();
        MODULE_SERVER_UNIT.server.getConnections().forEach(connection -> stringBuilder.append(connection).append('\n'));
        return stringBuilder.toString();
    }

    private @NotNull @Unmodifiable Object cmd_setprop(@NotNull Args args) {
        int gameObjectId = args.get(int.class, 0);
        IGameObject gameObject = WORLD_SCENE.getWorldByGameObjectId(gameObjectId).getGameObjectById(gameObjectId);
        String prop = args.get(String.class, 1) + "=" + args.get(String.class, 2);
        setProperties(gameObject, DataEntry.newInstance(prop));
        return getProperties(gameObject).toString();
    }

    private @NotNull Object cmd_tw(@NotNull Args args) {
        int playerId = args.get(int.class, 0);
        String roomId = args.get(String.class, 1);
        float x = args.get(float.class, 2, 64f);
        float y = args.get(float.class, 3, 64f);
        WORLD_SCENE.instantSwitchRoomForPlayerActor(playerId, roomId, x, y);
        return "";
    }

    private @Nullable Object cmd_kill(@NotNull Args args) {
        WORLD_SCENE.getPlayerActorByPlayerId(args.get(int.class, 0)).ifPresent(playerActor -> playerActor.setHealth(0));
        return "";
    }

    private @Nullable Object cmd_tostring(@NotNull Args args) {
        int gameObjectId = args.get(int.class, 0);
        if (gameObjectId == 0) return null;

        World world = WORLD_SCENE.getWorldByGameObjectId(gameObjectId);
        if (world == null) {
            return null;
        }

        IGameObject gameObject = world.getGameObjectById(gameObjectId);
        String result = world.toString() + "\n--\n"
                + gameObject.toString() + "\n--\n"
                + getProperties(gameObject) + "\n--\n"
                + gameObject.getMapkitItem().getDataEntry();
        return result;
    }

    private Object cmd_fps(Args args) {
        try {
            int fps = WORLD_SCENE.getFps();
            System.out.println(fps);
            return fps;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private Object cmd_unban(Args args) {
        try {
            String a = args.get(String.class, 0);
            String ip = a.contains(".") ? a : IConnection.getIpFromAddress(MODULE_GENERAL.getConnection(args.get(int.class, 0)).orElseThrow().getRemoteAddress());
            BANLIST.unban(ip);
            String result = "Unbanned ip " + ip;
            System.out.println(result);
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull Object cmd_ban(@NotNull Args args) {
        try {
            String a = args.get(String.class, 0);
            String ip = a.contains(".") ? a : IConnection.getIpFromAddress(MODULE_GENERAL.getConnection(args.get(int.class, 0)).orElseThrow().getRemoteAddress());
            BANLIST.ban(ip);
            String result = "Banned ip " + ip;
            System.out.println(result);

            MODULE_SERVER_UNIT.server.getConnections().stream().filter(c -> {
                String connectionIp = IConnection.getIpFromAddress(c.getRemoteAddress());
                return connectionIp.equals(ip);
            }).findAny().orElseThrow().closeIfOpen();

            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull @Unmodifiable Object cmd_banlist(Args args) {
        StringBuilder s = new StringBuilder();
        BANLIST.getList().forEach(ip -> s.append(ip).append('\n'));
        System.out.println(s);
        return s.toString();
    }

    private Object cmd_setmap(Args args) {
        try {
            String mapName = args.get(String.class, 0);
            MODULE_GENERAL.setMap(mapName);
            return "set map '" + mapName + "'";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    private @NotNull Object cmd_syncmapkit(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 0);
            String mapkitName = args.get(String.class, 1);
            MODULE_CONTENT_MANAGER.syncSendMapkit(mapkitName, playerId);
            return "sync mapkit '" + mapkitName + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @NotNull Object cmd_syncmap(@NotNull Args args) {
        try {
            int playerId = args.get(int.class, 0);
            String mapName = args.get(String.class, 1);
            MODULE_CONTENT_MANAGER.syncSendMap(mapName, playerId);
            return "sync map '" + mapName + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
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
            return "sync path '" + path + "' with player " + playerId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private @Nullable Object cmd_exit(Args args) {
        MODULE_GENERAL.exit();
        return null;
    }

    private @Unmodifiable Object cmd_players(Args args) {
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
        new Thread(repl::start, "repl").start();
    }
}
