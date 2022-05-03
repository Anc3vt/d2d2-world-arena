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
        var a = new Args(textureCoords, DELIMITER);
        int x = a.next(int.class);
        int y = a.next(int.class);
        int w = a.next(int.class);
        int h = a.next(int.class);
        return getMapkitItem().getTextureAtlas().createTexture(x, y, w, h);
    }


}
