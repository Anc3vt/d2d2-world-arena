package com.ancevt.d2d2world.editor.dev;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.D2D2WorldAssets;
import com.ancevt.d2d2world.editor.ui.mapkitpanel.MapkitFrame;
import com.ancevt.d2d2world.editor.ui.mapkitpanel.MapkitPanel;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Tests_MapkitPanelDev {


    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.init();
        D2D2WorldAssets.load();

        MapIO.setMapkitsDirectory("/home/ancevt/java/workspace/d2d2-world-arena/d2d2-world-arena-server/data/mapkits/");
        MapIO.setMapsDirectory("/home/ancevt/java/workspace/d2d2-world-arena/d2d2-world-arena-server/data/maps/");

        Async.run(() -> {
            try {
                GameMap gameMap = MapIO.load("map0.wam");
                mapLoaded(gameMap);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        });


        D2D2.loop();
    }

    private static void mapLoaded(GameMap gameMap) {
        log.info("map loaded: " + gameMap);

        MapkitFrame mapkitFrame = new MapkitFrame();
        mapkitFrame.setManualResizable(true);
        D2D2.stage().add(mapkitFrame, 100, 150);

        MapkitPanel mapkitPanel = mapkitFrame.getMapkitPanel();

        MapkitManager.getInstance().keySet().forEach(mapkitName -> {
            Mapkit mapkit = MapkitManager.getInstance().getMapkit(mapkitName);

            if(mapkit != BuiltInMapkit.getInstance() && mapkit != AreaMapkit.getInstance()) {
                mapkitPanel.addMapkit(mapkit);
            }
        });

    }
}
