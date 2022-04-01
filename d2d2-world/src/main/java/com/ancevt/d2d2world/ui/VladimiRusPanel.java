package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;

public class VladimiRusPanel extends DisplayObjectContainer {

    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    public static final int TYPE_4 = 4;

    private final Sprite left;
    private final Sprite middle;
    private final Sprite right;
    private float width;

    public VladimiRusPanel(int type) {
        left = new Sprite("d2d2-world-common-tileset-vrpanel" + type + "-left");
        middle = new Sprite("d2d2-world-common-tileset-vrpanel" + type + "-middle");
        right = new Sprite("d2d2-world-common-tileset-vrpanel" + type + "-right");

        add(left);
        add(right);
        add(middle);

        setWidth(64);
    }

    @Override
    public float getWidth() {
        return width;
    }

    private void setWidth(float width) {
        this.width = width;
        middle.setScaleX(width - left.getWidth() - right.getWidth());
        middle.setX(left.getWidth());
        right.setX(left.getWidth() + middle.getWidth() * middle.getScaleX());
    }

    private void setColor(Color color) {
        left.setColor(color);
        middle.setColor(color);
        right.setColor(color);
    }

    private Color getColor() {
        return left.getColor();
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        D2D2World.init(true);

        final VladimiRusPanel panel1 = new VladimiRusPanel(TYPE_1);
        panel1.setWidth(64f);
        panel1.setColor(Color.YELLOW);
        //panel1.setScale(2f, 2f);
        root.add(panel1, 0, 0);

        final VladimiRusPanel panel2 = new VladimiRusPanel(TYPE_2);
        panel2.setWidth(64f);
        panel2.setColor(Color.ORANGE);
        //panel2.setScale(2f, 2f);
        root.add(panel2, 0, 100);

        final VladimiRusPanel panel3 = new VladimiRusPanel(TYPE_3);
        panel3.setWidth(96f);
        panel3.setColor(Color.LIGHT_GREEN);
        //panel3.setScale(2f, 2f);
        root.add(panel3, 0, 200);

        final VladimiRusPanel panel4 = new VladimiRusPanel(TYPE_4);
        panel4.setWidth(128f);
        panel4.setColor(Color.WHITE);
        //panel4.setScale(2f, 2f);
        root.add(panel4, 0, 300);

        D2D2.loop();
    }
}
