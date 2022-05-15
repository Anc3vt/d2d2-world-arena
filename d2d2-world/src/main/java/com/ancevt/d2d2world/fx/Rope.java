
package com.ancevt.d2d2world.fx;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.D2D2WorldAssets;

public class Rope extends Sprite {

    private float degree;
    private float length;

    public Rope(Texture texture) {
        setTexture(texture);
    }

    public void setDegree(float degree) {
        this.degree = degree;
        setRotation(degree);
    }

    public float getDegree() {
        return degree;
    }

    public void setLength(float length) {
        this.length = length;
        int repeat = (int) (length / getTexture().width());
        setRepeatX(repeat);
        System.out.println(getRepeatX());
    }

    public float getLength() {
        return length;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2World.init(false, false);

        root.getStage().setScaleMode(ScaleMode.EXTENDED);

        Rope rope = new Rope(D2D2WorldAssets.getRopeTexture());
        rope.setLength(100);
        rope.setDegree(60);

        root.add(rope, 100, 100);

        D2D2.loop();
    }
}
