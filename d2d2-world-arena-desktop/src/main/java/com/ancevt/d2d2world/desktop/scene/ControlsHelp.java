package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;

public class ControlsHelp extends Sprite {

    private static final int SPEED = 25;

    private int direction = SPEED;

    private int tact;

    public ControlsHelp() {
        super(D2D2World.getControlsHelpTexture());
        setColor(new Color(0xFF, 0x80, 0xFF));
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        int value = getColor().getB();

        getColor().setB(value + direction);

        value = getColor().getB();

        if (value > 0xFF) {
            direction = -SPEED;
        } else if (value < 0) {
            direction = SPEED;
        }

        tact++;

        if(tact > 250) {
            setAlpha(getAlpha() - 0.075f);
            if(getAlpha() <= 0.01f) {
                removeFromParent();
            }
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        D2D2World.init(false, false);
        root.setBackgroundColor(Color.DARK_GRAY);

        root.add(new ControlsHelp(), 0, 0);
        root.setScale(3f, 3f);

        D2D2.loop();
    }
}
