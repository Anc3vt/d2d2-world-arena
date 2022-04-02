/*
 *   D2D2 World Editor
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
package com.ancevt.d2d2world.editor;

import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.media.Sound;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.debug.DebugPanel;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanel;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import com.ancevt.util.args.Args;

import java.io.IOException;

public class D2D2WorldEditorMain {
    public static void main(String[] args) throws IOException {
        Args a = new Args(args);

        if (a.contains("--disable-sound")) {
            Sound.setEnabled(false);
        }

        UnixDisplay.setEnabled(a.contains("--colorize-logs"));

        MapIO.mapkitsDirectory = a.get("--mapkits-directory", "/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/");
        MapIO.mapsDirectory = a.get("--maps-directory", "/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/maps/");
        MapIO.mapFileName = a.get("--map-filename", "map0.wam");

        D2D2.init(new LWJGLStarter(1000, 700, "D2D2 World Editor"));
        D2D2World.init(true);

        // BitmapFont.loadDefaultBitmapFont("PressStart2P.bmf");
        Root root = D2D2.getStage().getRoot();
        D2D2.setSmoothMode(false);

        DisplayObjectContainer cameraLayer = new DisplayObjectContainer();

        GameMap map = MapIO.load(MapIO.mapFileName);

        World world = new World();
        cameraLayer.add(world);

        world.setMap(map);
        world.setRoomRectVisible(true);

        root.add(cameraLayer);

        world.setAreasVisible(true);

        cameraLayer.setXY(D2D2.getStage().getWidth() / 2, D2D2.getStage().getHeight() / 2);

        EditorContainer editorContainer = new EditorContainer(root, world);
        root.add(editorContainer);

        D2D2.getStage().addEventListener(Event.RESIZE, e -> {
            editorContainer.getGrid().redrawLines();
            cameraLayer.setXY(D2D2.getStage().getWidth() / 2, D2D2.getStage().getHeight() / 2);
            MapkitToolsPanel.getInstance().setX(D2D2.getStage().getWidth()
                    - MapkitToolsPanel.getInstance().getWidth() - 10);

            world.getCamera().setViewportSize(D2D2.getStage().getWidth(), D2D2.getStage().getHeight());
        });

        cameraLayer.setScale(2f, 2f);

        root.addEventListener(InputEvent.MOUSE_WHEEL, e -> {
            if(editorContainer.isMouseAtPanels(Mouse.getX(), Mouse.getY())) return;

            InputEvent inputEvent = (InputEvent) e;

            float scale = cameraLayer.getScaleX();

            if (inputEvent.getDelta() > 0) {
                scale += 0.25f;
            } else {
                scale -= 0.25f;
            }

            if (scale < 0.25f) scale = 0.25f;

            cameraLayer.setScale(scale, scale);
            cameraLayer.setXY(D2D2.getStage().getWidth() / 2, D2D2.getStage().getHeight() / 2);
            editorContainer.setInfoText("Zoom: " + cameraLayer.getScaleX());
            editorContainer.getGrid().redrawLines();
        });

        for (String mapkitName : MapkitManager.getInstance().keySet()) {
            MapkitToolsPanel.getInstance().addMapkit(
                    MapkitManager.getInstance().getMapkit(mapkitName)
            );
        }

        root.add(new FpsMeter(), 5, 0);
        D2D2.getStage().setScaleMode(ScaleMode.REAL);
        D2D2.loop();
        DebugPanel.saveAll();
        System.exit(0);
    }

}
