package com.ancevt.d2d2world.tools.atlastiler;

import com.ancevt.commons.properties.PropertyWrapper;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import static com.ancevt.d2d2.D2D2.getStage;
import static com.ancevt.d2d2.D2D2.getTextureManager;

public class AtlasTiler {

    public static final String PROPERTY_INPUT_ATLAS = "atlas-tiler.input-atlas";

    public static void main(String[] args) throws FileNotFoundException {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Properties properties = PropertyWrapper.argsToProperties(args, new Properties());

        String inputFile = (String) properties.getOrDefault("in", "tileset.png");
        String outputFile = (String) properties.getOrDefault("out", "tileset.dat");

        TextureAtlas atlas = getTextureManager().loadTextureAtlas(new FileInputStream(inputFile));
        Sprite sprite = new Sprite(atlas.createTexture());

        DisplayObjectContainer canvasContainer = new DisplayObjectContainer();
        Canvas canvas = new Canvas();



        getStage().addEventListener(Event.RESIZE, Event.RESIZE, event ->
            canvasContainer.setXY(getStage().getWidth() / 2, getStage().getHeight() / 2)
        );

        System.out.println(inputFile);
        root.add(new FpsMeter());
        D2D2.loop();
    }
}
