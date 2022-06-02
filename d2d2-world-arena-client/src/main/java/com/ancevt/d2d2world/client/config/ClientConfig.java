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
package com.ancevt.d2d2world.client.config;

import com.ancevt.util.config.ListConfig;

public class ClientConfig extends ListConfig {

    public static final String SERVER = "server";
    public static final String PLAYERNAME = "playername";
    public static final String RCON_PASSWORD = "rcon-password";
    public static final String DEBUG_WORLD_ALPHA = "debug.world-alpha";
    public static final String DEBUG_GAME_OBJECT_IDS = "debug.game-object-ids";
    public static final String DEBUG_CHARACTER = "debug.character-mapkit-item";
    public static final String AUTO_ENTER = "auto-enter";
    public static final String DISPLAY_FULLSCREEN = "display.fullscreen";
    public static final String DEBUG_WINDOW_SIZE = "debug.window-size";
    public static final String SOUND_ENABLED = "sound-enabled";
    public static final String DEBUG_WINDOW_XY = "debug.window-xy";
    public static final String DISPLAY_MONITOR = "display.monitor";
    public static final String DISPLAY_RESOLUTION = "display.resolution";

    private static final String PATH = "d2d2-world-arena-client.conf";

    public static final ClientConfig CONFIG = new ClientConfig();

    public ClientConfig() {
        super(PATH);
    }
}
