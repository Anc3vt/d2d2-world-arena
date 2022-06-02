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
package com.ancevt.d2d2world.server.config;

import com.ancevt.util.config.ListConfig;

public class ServerConfig extends ListConfig {

    public static final String SERVER_NAME = "server.name";
    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_CONNECTION_TIMEOUT = "server.connection-timeout";
    public static final String SERVER_LOOP_DELAY = "server.loop-delay";
    public static final String SERVER_MAX_PLAYERS = "world.max-players";
    public static final String RCON_PASSWORD = "rcon.password";
    public static final String WORLD_DEFAULT_MAP = "world.default-map";
    public static final String WORLD_DEFAULT_MOD = "world.default-mod";
    public static final String CONTENT_COMPRESSION = "content.compression";
    public static final String DEBUG_FORCED_SPAWN_AREA = "debug.forced-spawn-area";

    private static final String PATH = "d2d2-world-arena-server.conf";

    public static final ServerConfig CONFIG = new ServerConfig();

    public ServerConfig() {
        super(PATH);
    }

}
