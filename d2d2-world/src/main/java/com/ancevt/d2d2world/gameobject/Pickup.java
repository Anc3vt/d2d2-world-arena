package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class Pickup extends DisplayObjectContainer implements ICollision {

    private final Sprite bubbleSprite;
    private final DisplayObjectContainer container;
    private final Sprite icon;

    private int counter = 0;
    private boolean ready;

    public Pickup(MapkitItem mapkitItem, int gameObjectId) {
        setMapkitItem(mapkitItem);
        setGameObjectId(gameObjectId);
        container = new DisplayObjectContainer();
        bubbleSprite = new Sprite(D2D2World.getPickupBubbleTexture());
        bubbleSprite.setAlpha(0.75f);
        icon = new Sprite("pickup-health25");
        container.add(icon);
        icon.setXY(-icon.getWidth() / 2, -icon.getHeight() / 2 + 1);
        container.add(bubbleSprite, -16, -16);
        container.setScale(0.01f, 0.01f);
        add(container);

        setCollision(-16, -16, 32, 32);
    }
    public void setTexture(Texture texture) {
        icon.setTexture(texture);
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public void process() {

    }

    @Override
    public void onEachFrame() {
        if (!ready) {
            container.toScale(1.1f, 1.1f);
            if (container.getScaleX() >= 1) {
                container.setScale(1, 1);
                ready = true;
            }
        } else {

            counter++;
            final float factor = 0.0025f;
            if (counter < 20) {
                container.setScale(container.getScaleX() + factor, container.getScaleY() - factor);
                icon.moveY(-0.01f);
                container.moveY(0.25f);
            } else {
                container.setScale(container.getScaleX() - factor, container.getScaleY() + factor);
                icon.moveY(+0.01f);
                container.moveY(-0.25f);
            }

            if (counter > 40) {
                counter = 0;
                container.setScale(1, 1);
                icon.setScale(1, 1);
                icon.setXY(-icon.getWidth() / 2, -icon.getHeight() / 2);
                container.setXY(0, 0);
            }
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        D2D2World.init(true);

        Pickup pickup = new Pickup(null, 0);
        pickup.setScale(1.5f, 1.5f);

        root.add(pickup, 100, 100);

        root.add(new FpsMeter());
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.loop();
    }

}
