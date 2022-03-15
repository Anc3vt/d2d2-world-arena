package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class StandardBullet extends Bullet implements ITight {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    private boolean setToRemove;

    public StandardBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
    }

    @Override
    public void prepare() {

    }

    @Override
    public void destroy() {
        setSpeed(0);
        setToRemove = true;
    }

    @Override
    public void process() {
        if (setToRemove && isOnWorld()) {
            toScale(1.1f, 1.1f);
            toAlpha(0.8f);
            rotate(10);
            setCollisionEnabled(false);
            if(getAlpha() <= 0.1f) {
                getWorld().removeGameObject(this, false);
            }
        }

        moveX(getSpeed() * getDirection());
        super.process();
    }

    @Override
    public void setFloorOnly(boolean b) {

    }

    @Override
    public boolean isFloorOnly() {
        return false;
    }

    @Override
    public void setPushable(boolean b) {

    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
