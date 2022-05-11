/*
 *   D2D2 World Arena Client
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.client.scene;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;

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
