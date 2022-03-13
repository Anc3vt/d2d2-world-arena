/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.FramedDisplayObjectContainer;
import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.IFramedDisplayObject;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureManager;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_IFramed {

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, D2D2Demo_IFramed.class.getName() + " (floating)"));
        Root root = D2D2.getStage().getRoot();

        ISprite[] frames = new ISprite[]{
                new Sprite("frame0"),
                new Sprite("frame1"),
                new Sprite("frame2"),
                new Sprite("frame3"),
                new Sprite("frame4"),
                new Sprite("frame5"),
                new Sprite("frame6"),
                new Sprite("frame7"),
                new Sprite("frame8"),
                new Sprite("frame9"),
                new Sprite("frame10"),
                new Sprite("frame11"),
        };

        IFramedDisplayObject framedDisplayObject = new FramedDisplayObjectContainer(frames, true);
        framedDisplayObject.setLoop(true);
        framedDisplayObject.play();
        root.add(framedDisplayObject);

        TextureManager tm = D2D2.getTextureManager();

        Texture[] frameTextures = new Texture[]{
                tm.getTexture("frame0"),
                tm.getTexture("frame1"),
                tm.getTexture("frame2"),
                tm.getTexture("frame3"),
                tm.getTexture("frame4"),
                tm.getTexture("frame5"),
                tm.getTexture("frame6"),
                tm.getTexture("frame7"),
                tm.getTexture("frame8"),
                tm.getTexture("frame9"),
                tm.getTexture("frame10"),
                tm.getTexture("frame11"),
        };

        framedDisplayObject = new FramedSprite(frameTextures);
        framedDisplayObject.setLoop(true);
        framedDisplayObject.play();
        framedDisplayObject.setSlowing(6);
        root.add(framedDisplayObject, 0, 16);

        D2D2.loop();
    }
}
