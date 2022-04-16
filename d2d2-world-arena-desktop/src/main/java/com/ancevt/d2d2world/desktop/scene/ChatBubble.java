package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.display.Sprite;

public class ChatBubble extends Sprite {

    private float factor = -0.05f;

    public ChatBubble() {
        super("d2d2-world-common-tileset-chat-hint");
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        setAlpha(getAlpha() + factor);

        if(getAlpha() < 0.0f || getAlpha() > 1.0f) factor = -factor;
    }

}
