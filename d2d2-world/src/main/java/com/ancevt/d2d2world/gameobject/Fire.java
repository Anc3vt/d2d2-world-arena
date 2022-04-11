package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.debug.DebugPanel;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class Fire extends DisplayObjectContainer implements IDamaging {

    private static final float MAX_VALUE = 99.0f;

    private static final float SPEED = 0.25f;

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    private boolean collisionEnabled;
    private float collisionX;
    private float collisionY;
    private float collisionWidth;
    private float collisionHeight;
    private World world;

    private float value;

    private final FramedSprite framed;

    private int tact = 0;

    public Fire(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        framed = new FramedSprite(mapkitItem
                .getTextureAtlas()
                .createTextures(mapkitItem.getDataEntry().getString(DataKey.IDLE))
        );
        framed.setLoop(false);
        framed.setFrame(0);
        add(framed, -framed.getWidth() / 2, -framed.getHeight());
        value = MAX_VALUE;
    }

    @Property
    public void setValue(float value) {
        if (value > MAX_VALUE) value = MAX_VALUE;
        else if (value < 0) value = 0;
        this.value = value;
        update();
    }

    @Property
    public float getValue() {
        return value;
    }

    @Override
    public void process() {
        value -= SPEED * Math.random();
        if (value <= 0) {
            if (isOnWorld()) {
                getWorld().removeGameObject(this, false);
            } else {
                removeFromParent();
            }
        }
        update();
    }

    private void update() {
        tact++;
        int animationShift = tact < 5 ? 1 : 0;
        if (tact >= 10) {
            tact = 0;
            float scale = 1f + ((float) Math.random()) / 4;
            setScale(scale, scale);
            if (Math.random() > 0.5) {
                setScaleX(-getScaleX());
            }
        }

        float frameCount = framed.getFrameCount() - 1;
        int frame = (int) (frameCount * (value / 100.0f));
        framed.setFrame(frame + animationShift);
    }

    @Override
    public void setCollisionEnabled(boolean value) {
        this.collisionEnabled = value;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
        this.collisionWidth = collisionWidth;
    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public void setCollisionX(float collisionX) {
        this.collisionX = collisionX;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public void setCollisionY(float collisionY) {
        this.collisionY = collisionY;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
    }

    @Override
    public void setDamagingPower(int damagingPower) {

    }

    @Override
    public int getDamagingPower() {
        return 5;
    }

    @Override
    public void setDamagingOwnerActor(Actor actor) {

    }

    @Override
    public Actor getDamagingOwnerActor() {
        return null;
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        MapIO.setMapkitsDirectory("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/");
        MapIO.setMapsDirectory("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/maps/");
        D2D2World.init(false);

        root.setBackgroundColor(Color.of(0x223344));

        Fire fire = (Fire) BuiltInMapkit.getInstance().getItem("fire").createGameObject(0);


        DisplayObjectContainer doc = new DisplayObjectContainer();
        doc.setScale(3, 3);
        doc.add(fire, 100, 100);

        root.add(doc);

        DebugPanel.setEnabled(true);

        fire.addEventListener(ActorEvent.EACH_FRAME, e -> {
            fire.process();
            DebugPanel.createIfEnabled("test2", fire.getValue());
        });


        D2D2.loop();
        DebugPanel.saveAll();
    }
}























