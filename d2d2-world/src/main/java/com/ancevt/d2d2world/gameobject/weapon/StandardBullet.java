package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class StandardBullet extends Bullet {

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
        setToRemove = true;
    }

    @Override
    public void process() {
        if (setToRemove && isOnWorld()) getWorld().removeGameObject(this, false);

        moveX(getSpeed() * getDirection());
        super.process();
    }
}
