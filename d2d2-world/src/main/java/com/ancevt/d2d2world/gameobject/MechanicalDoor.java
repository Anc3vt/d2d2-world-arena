package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class MechanicalDoor extends Sprite implements
        ISynchronized,
        ISonicSynchronized,
        ITight,
        IResettable {

    private static final float OPEN_SPEED = 4;

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    private boolean collisionEnabled;
    private float collisionWidth;
    private float collisionHeight;
    private float collisionX;
    private float collisionY;
    private World world;

    private boolean pushable;
    private boolean floorOnly;

    private int timeTacts;
    private boolean opening;
    private boolean closing;
    private float closedCollisionWidth;
    private float closedCollisionHeight;

    private int tact;

    public MechanicalDoor(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem.getTexture());
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
        setCollisionEnabled(true);
    }

    @Override
    public void onCollide(ICollision collideWith) {
        if(collideWith instanceof Actor || collideWith instanceof Weapon.Bullet) {
            if(collisionHeight > 0) open();
        }
    }

    public void open() {
        closing = false;
        opening = true;
        if (mapkitItem.getDataEntry().containsKey(DataKey.OPEN_SOUND)) {
            playSound(mapkitItem.getDataEntry().getString(DataKey.OPEN_SOUND));
        }
    }

    public void close() {
        closing = true;
        if (mapkitItem.getDataEntry().containsKey(DataKey.CLOSE_SOUND)) {
            playSound(mapkitItem.getDataEntry().getString(DataKey.CLOSE_SOUND));
        }
    }

    @Override
    public void process() {
        if (opening) {
            collisionHeight -= OPEN_SPEED;
            if(collisionHeight < 0) collisionHeight = 0;
            setTexture(mapkitItem.getTexture().getSubtexture(0, 0, (int) collisionWidth, (int) collisionHeight));

            if (collisionHeight <= 0) {
                opening = false;
                tact = getTimeTacts();
            }
        }

        if (closing) {
            collisionHeight += OPEN_SPEED;
            setTexture(mapkitItem.getTexture().getSubtexture(0, 0, (int) collisionWidth, (int) collisionHeight));

            if (collisionHeight >= closedCollisionHeight) {
                closing = false;
            }
        }

        if (tact > 0) {
            tact--;

            if (tact == 0) {
                close();
            }
        }
    }

    @Property
    public int getTimeTacts() {
        return timeTacts;
    }

    @Property
    public void setTimeTacts(int timeTacts) {
        this.timeTacts = timeTacts;
    }

    @Property
    public void setClosedCollisionWidth(float closedCollisionWidth) {
        this.closedCollisionWidth = closedCollisionWidth;
    }

    @Property
    public float getClosedCollisionWidth() {
        return closedCollisionWidth;
    }

    @Property
    public void setClosedCollisionHeight(float closedCollisionHeight) {
        this.closedCollisionHeight = closedCollisionHeight;
    }

    @Property
    public float getClosedCollisionHeight() {
        return closedCollisionHeight;
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void reset() {
        if (isOnWorld()) getWorld().getSyncDataAggregator().reset(this);
        setCollisionWidth(closedCollisionWidth);
        setCollisionHeight(closedCollisionHeight);
        setTexture(mapkitItem.getTexture());
        opening = false;
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {

    }

    @Override
    public boolean isPermanentSync() {
        return true;
    }

    @Override
    public void setFloorOnly(boolean b) {
        floorOnly = b;
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean b) {
        pushable = b;
    }

    @Override
    public boolean isPushable() {
        return pushable;
    }

    @Override
    public void setCollisionEnabled(boolean value) {
        collisionEnabled = value;
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

}
