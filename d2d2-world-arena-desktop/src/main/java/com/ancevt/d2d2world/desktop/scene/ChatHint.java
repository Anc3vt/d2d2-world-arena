package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;

public class ChatHint extends Sprite {

    private float factor = -0.05f;

    public ChatHint() {
        super("d2d2-world-common-tileset-chat-hint");
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        setAlpha(getAlpha() + factor);

        if(getAlpha() < 0.0f || getAlpha() > 1.0f) factor = -factor;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        D2D2World.init(true);

        root.add(new ChatHint());

        root.add(new FpsMeter());
        D2D2.loop();
    }
}
