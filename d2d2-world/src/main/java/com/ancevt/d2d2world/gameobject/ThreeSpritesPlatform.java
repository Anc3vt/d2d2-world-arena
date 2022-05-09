/*
 *   D2D2 World
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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.util.args.Args;

public class ThreeSpritesPlatform extends Platform {

    private static final String DELIMITER = ",";

    private Sprite spriteLeft;
    private Sprite spriteCenter;
    private Sprite spriteRight;
    private float width;

    public ThreeSpritesPlatform(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        addEventListener(ThreeSpritesPlatform.class, Event.ADD_TO_STAGE, this::this_addToStage);
        setWidth(48);
    }

    private void this_addToStage(Event event) {
        removeEventListener(ThreeSpritesPlatform.class);
        rebuild();
    }

    private void rebuild() {
        if (spriteLeft != null && spriteCenter != null && spriteRight != null) {
            spriteCenter.setX(spriteLeft.getWidth());
            int repeatCenter = (int) (width / spriteCenter.getTexture().width()) - 2;
            spriteCenter.setRepeatX(repeatCenter);
            spriteRight.setX(spriteLeft.getWidth() + spriteCenter.getTexture().width() * repeatCenter);
        }
    }

    @Property
    public void setWidth(float width) {
        this.width = width;
        rebuild();
    }

    @Property
    public float getWidth() {
        return width;
    }

    @Property
    public void setSpriteLeft(String textureCoords) {
        spriteLeft = new Sprite(createTexture(textureCoords));
        add(spriteLeft);
    }

    @Property
    public void setSpriteCenter(String textureCoords) {
        spriteCenter = new Sprite(createTexture(textureCoords));
        add(spriteCenter);
    }

    @Property
    public void setSpriteRight(String textureCoords) {
        spriteRight = new Sprite(createTexture(textureCoords));
        add(spriteRight);
    }

    @Override
    public float getCollisionX() {
        return 0;
    }

    @Override
    public float getCollisionY() {
        return 0;
    }

    @Override
    public float getCollisionWidth() {
        return getWidth();
    }

    @Override
    public float getCollisionHeight() {
        return getHeight();
    }

    private Texture createTexture(String textureCoords) {
        var a = Args.of(textureCoords, DELIMITER);
        int x = a.next(int.class);
        int y = a.next(int.class);
        int w = a.next(int.class);
        int h = a.next(int.class);
        return getMapkitItem().getTextureAtlas().createTexture(x, y, w, h);
    }


}
