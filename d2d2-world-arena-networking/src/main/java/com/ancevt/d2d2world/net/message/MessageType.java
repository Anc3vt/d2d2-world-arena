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
     * b)t f)x f)y
     */
    public static final int CLIENT_PLAYER_XY = 1;

    /**
     * b)t f)aimX f)aimY
     */
    public static final int CLIENT_PLAYER_AIM_XY = 2;

    /**
     * b)t s)S)headers
     */
    public static final int CLIENT_REQUEST_FILE = 7;

    /**
     * b)t s)damageValue
     */
    public static final int CLIENT_HEALTH_REPORT = 8;


    /**
     * b)t i)hookGameObjectId
     */
    public static final int CLIENT_PLAYER_HOOK = 9;
    //                                           SERVER ===> CLIENT

    /**
     * b)t B)DATA
     */
    public static final int SERVER_SYNC_DATA = 120;


    /**
     * b)t)
     */
    public static final int SERVER_PLAYER_ATTACK = 121;

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

    /**
     * b)t
     */
    public static final int HANDSHAKE = 148;

}
