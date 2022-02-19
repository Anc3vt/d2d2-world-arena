/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.ScaleMode;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2world.desktop.scene.IntroRoot;
import ru.ancevt.d2d2world.desktop.ui.chat.Chat;
import ru.ancevt.d2d2world.net.client.Client;

import java.io.IOException;
import java.util.Properties;

import static ru.ancevt.d2d2world.desktop.ModuleContainer.modules;

public class D2D2WorldDesktopMain {

    public static void main(String[] args) throws IOException {
        // Load config properties
        Config config = new Config();
        config.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                config.setProperty(key, value);
            }
        }

        System.out.println(config);

        // Load project properties
        Properties properties = new Properties();
        properties.load(D2D2WorldDesktopMain.class.getClassLoader().getResourceAsStream("project.properties"));
        String projectName = properties.getProperty("project.name");
        String version = properties.getProperty("project.version");


        String autoEnterPlayerName = config.getString(Config.PLAYER, "");

        D2D2.init(new LWJGLStarter(900, 600, "(floating) D2D2 World Arena " + autoEnterPlayerName));

        // Module initialization section: THE ORDER IS IMPORTANT!
        modules.add(config);
        modules.add(new Client());
        modules.add(new Chat());
        modules.add(new ClientCommandProcessor());
        //

        IntroRoot introRoot = new IntroRoot(projectName + " " + version);

        if (!autoEnterPlayerName.isEmpty()) {
            introRoot.addEventListener(Event.ADD_TO_STAGE, e -> {
                introRoot.enter(
                        config.getString(Config.SERVER, "localhost:2245"), autoEnterPlayerName);
            });
        }

        D2D2.getStage().setRoot(introRoot);
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.loop();
        System.exit(0);
    }

}
