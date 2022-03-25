/*
 *   D2D2 World Arena Networking
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
package com.ancevt.d2d2world.net.message;

public class MessageType {

    //                                        CLIENT ===> SERVER

    /**
     * b)t b)controllerState
     */
    public static final int CLIENT_PLAYER_CONTROLLER = 3;


    /**
     * b)t b)delta + 1
     */
    public static final int CLIENT_PLAYER_WEAPON_SWITCH = 4;
    /**
     * b)t f)aimX f)aimY
     */
    public static final int CLIENT_PLAYER_AIM_XY = 2;

    /**
     * b)t s)S)headers
     */
    public static final int CLIENT_REQUEST_FILE = 7;

    //                                           CLIENT <=== SERVER

    /**
     * b)t B)DATA
     */
    public static final int SERVER_SYNC_DATA = 120;

    //                                           CLIENT <==> SERVER

    /**
     * b)t
     */
    public static final int PING = 197;

    /**
     * File or part of file
     * b)t s)S)headers i)contentLength L)data
     */
    public static final int FILE_DATA = 198;

    /**
     * b)t i)S)json
     */
    public static final int DTO = 200;


}