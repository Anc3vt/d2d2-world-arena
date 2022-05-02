package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;

public class ChatBubble extends Sprite {

    private float factor = -0.05f;

    public ChatBubble() {
        super(D2D2WorldArenaDesktopAssets.getChatBubbleTexture());
    }

    @Override
    public void onEachFrame() {
        setAlpha(getAlpha() + factor);
        if(getAlpha() < 0.0f || getAlpha() > 1.0f) factor = -factor;
    }

}
