/*
 *   D2D2 World Client
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
package ru.ancevt.d2d2world.net.message;

public class MessageType {

    /*
     Protocol message order:

    1. At first, when new player entering server:

        1.1 new player client  ---  connection established ---  server
        1.2 new player client  ==>  PLAYER_ENTER_REQUEST(name)  ==>  server
        1.3 new player client  <==  PLAYER_ENTER_RESPONSE(id)   <==  server

    2. Than new player need to know about all other players, and all other players need to know about new player

        2.1 Server sends all player data of each other players to new player (ids, colors, names)

        2.2. all players {
            new player client <== PLAYER_INTRODUCE (other player info) <== server
        }

        2.3 Send info about new player to all others

        2.4  all players {
            other players clients <== PLAYER_INTRODUCE (new player info) <== server
        }

    3. Client start to send its state to server as reports on each render frame in

        3.1 infinity loop {
            new player client ==> PLAYER_CONTROLLER_AND_XY_REPORT ==> server
        }

    4. Server sending to player state off all others players (excluding new player, of course)

        4.1 all players infinite loop {
            new player client <== PLAYER_CONTROLLER_AND_XY <== server
        }

    5. Chat



    */

    //                                        CLIENT ===> SERVER

    /**
     * b)t
     */
    public static final int CLIENT_SERVER_INFO_REQUEST = 0;

    /**
     * b)t b) S)login b) S)passHash
     */
    public static final int CLIENT_RCON_LOGIN = 254;

    /**
     * b)t b) S)command text i) S)extraData
     */
    public static final int CLIENT_RCON_COMMAND = 255;

    /**
     * b)t b)L S)name  b) S)clientProtocolVersion i) S)extraData
     */
    public static final int CLIENT_PLAYER_ENTER_REQUEST = 1;

    /**
     * b) t
     */
    public static final int CLIENT_PLAYER_EXIT_REQUEST = 2;

    /**
     * b)t b)controllerState f)x f)y
     */
    public static final int CLIENT_PLAYER_CONTROLLER_AND_XY_REPORT = 3;

    /**
     * b)t b) S)chat msg text
     */
    public static final int CLIENT_PLAYER_TEXT_TO_CHAT = 4;

    /**
     * b)t
     */
    public static final int CLIENT_PLAYER_PING_REQUEST = 5;

    /**
     * b)t s)ping
     */
    public static final int CLIENT_PLAYER_PING_REPORT = 6;



    //                                           CLIENT <=== SERVER

    /**
     * b)t i) S)responseData
     */
    public static final int SERVER_RCON_RESPONSE = 253;


    /**
     * b)t b)S)serverName b)S)serverVersion b)S)serverProtocolVersion b)S) mapName b)S)mapkitName b)S)modeName
     * ( s)playerId b)S)playerName )...
     */
    public static final int SERVER_INFO_RESPONSE = 100;

    /**
     * b)t s) playerId b) playerName i)playerColor
     */
    public static final int SERVER_REMOTE_PLAYER_ENTER = 101;

    /**
     * b)t s)playerId i)color b) S)serverProtocolVersion
     */
    public static final int SERVER_PLAYER_ENTER_RESPONSE = 102;

    /**
     * b)t s)playerId b) S)name i)color i) S)extraData
     */
    public static final int SERVER_REMOTE_PLAYER_INTRODUCE = 103;

    /**
     * b)t i)chatMessageId b) S)text [ s) playerId b) S)playerName i)playerColor ]
     */
    public static final int SERVER_CHAT = 104;

    /**
     * b)t s)playerId b)controllerState f)x f)y
     */
    public static final int SERVER_REMOTE_PLAYER_CONTROLLER_AND_XY = 105;

    /**
     * b)t s)playerId b) exitReason
     */
    public static final int SERVER_REMOTE_PLAYER_EXIT = 106;

    /**
     * b)t
     */
    public static final int SERVER_PLAYER_PING_RESPONSE = 107;

    /**
     * b)t s)playerId s)pingValue
     */
    public static final int SERVER_REMOTE_PLAYER_PING_VALUE = 108;


    //                                           CLIENT <==> SERVER

    /**
     * b)t s)error code b) S)errorMessage i) S)errorDetailText
     */
    public static final int ERROR = 199;

    /**
     * b)t i) S)extraData
     */
    public static final int EXTRA = 200;


}