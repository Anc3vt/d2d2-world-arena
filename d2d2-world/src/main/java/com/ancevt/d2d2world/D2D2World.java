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

package com.ancevt.d2d2world;

public class D2D2World {

    public static final float ORIGIN_WIDTH = 800f;
    public static final float ORIGIN_HEIGHT = 600f;

    public static final float SCALE = 1.6f;//2.5f;

    private static boolean server;
    private static boolean editor;

    private D2D2World() {
    }

    public static void init(boolean server, boolean editor) {
        D2D2World.server = server;
        D2D2World.editor = editor;
        D2D2WorldAssets.load();
    }

    public static boolean isServer() {
        return server;
    }

    public static boolean isClient() {
        return !isServer();
    }

    public static boolean isEditor() {
        return editor;
    }



}
