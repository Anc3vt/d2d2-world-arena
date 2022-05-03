package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ShaderProgram;
import com.ancevt.d2d2.display.Sprite;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class D2D2Demo_ShaderProgram {


    @SneakyThrows
    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        root.add(new FpsMeter());

        Sprite sprite = new Sprite("satellite");
        sprite.setScale(5f, 5f);

        String vertexShader = Files.readString(Path.of("shader0.vert"));
        String fragmentShader = Files.readString(Path.of("shader0.frag"));

        sprite.setShaderProgram(new ShaderProgram(vertexShader, fragmentShader));

        root.add(sprite, 0, 0);
        root.setBackgroundColor(Color.DARK_BLUE);

        root.add(new Sprite("satellite"), 100, 100);

        D2D2.loop();
    }
}
