
package com.ancevt.d2d2world.editor;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.properties.PropertyWrapper;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanel;
import com.ancevt.d2d2world.editor.swing.JPropertiesEditor;
import com.ancevt.d2d2world.editor.util.ScreenUtils;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import com.ancevt.util.args.Args;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class D2D2WorldEditorMain {
    public static void main(String[] args) throws IOException {
        //Hack to avoid BadAccess bug with JPropertiesEditor under i3wm
        // <hack>
        var hack = JPropertiesEditor.create("(floating)", "", null);
        Async.runLater(1000, TimeUnit.MILLISECONDS, hack::dispose);
        // </hack>

        PropertyWrapper.argsToProperties(args, System.getProperties());

        Args a = Args.of(args);

        DebugPanel.setEnabled(a.contains("--debug"));

        SoundSystem.setEnabled(!a.contains("--disable-sound"));
        UnixDisplay.setEnabled(a.contains("--colorize-logs"));

        MapIO.setMapkitsDirectory(a.get("--mapkits-directory", "/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/"));
        MapIO.setMapsDirectory(a.get("--maps-directory", "/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/maps/"));
        MapIO.setMapFileName(a.get("--map-filename", "map0.wam"));

        var screenDimension = ScreenUtils.getDimension();
        D2D2.init(new LWJGLBackend(screenDimension.width(), screenDimension.height() - 300, "D2D2 World Editor (floating)"));
        D2D2.getBackend().setWindowXY(0, 40);
        D2D2World.init(true, true);

        // BitmapFont.loadDefaultBitmapFont("PressStart2P.bmf");
        Root root = D2D2.getStage().getRoot();
        D2D2.setSmoothMode(false);

        DisplayObjectContainer cameraLayer = new DisplayObjectContainer();
        cameraLayer.setName("Camera layer, plain DisplayObjectContainer");

        GameMap map = MapIO.load(MapIO.getMapFileName());

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


        // TODO: uncomment
        // cameraLayer.setScale(2f, 2f);


        root.addEventListener(InputEvent.MOUSE_WHEEL, e -> {
            if (editorContainer.isMouseAtPanels(Mouse.getX(), Mouse.getY())) return;

            if(world.isPlaying()) return;

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
