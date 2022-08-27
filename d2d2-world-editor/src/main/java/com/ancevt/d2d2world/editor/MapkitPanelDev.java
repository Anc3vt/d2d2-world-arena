package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.editor.ui.mapkitpanel.MapkitPanel;

public class MapkitPanelDev {

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.init();

        MapkitPanel mapkitPanel = new MapkitPanel();

        stage.add(mapkitPanel);


        D2D2.loop();
    }
}
