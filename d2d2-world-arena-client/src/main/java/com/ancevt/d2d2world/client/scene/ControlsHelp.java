
package com.ancevt.d2d2world.client.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;

public class ControlsHelp extends Sprite {

    private static final int SPEED = 25;

    private int direction = SPEED;

    private int tact;

    public ControlsHelp() {
        super(D2D2WorldArenaClientAssets.getControlsHelpTexture());
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
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        D2D2World.init(false, false);
        stage.setBackgroundColor(Color.DARK_GRAY);

        stage.add(new ControlsHelp(), 0, 0);
        stage.setScale(3f, 3f);

        D2D2.loop();
    }
}
