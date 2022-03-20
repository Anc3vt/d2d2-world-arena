package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class StandardBullet extends Bullet implements ITight {


    private boolean setToRemove;
    private boolean permanentSync;

    public StandardBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        addEventListener(StandardBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
    }

    private void this_addToStage(Event event) {
        removeEventListeners(StandardBullet.class);
        var a = getMapkitItem().getTextureAtlas();
        FramedSprite framedSprite = new FramedSprite(
                a.createTextures(getMapkitItem().getDataEntry().getString(DataKey.IDLE))
        );
        framedSprite.setFrame(0);
        framedSprite.setLoop(true);
        framedSprite.play();
        framedSprite.setXY(-framedSprite.getWidth() / 2, -framedSprite.getHeight() / 2);
        add(framedSprite);
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
            if (getAlpha() <= 0.1f) {
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

    @Override
    public boolean isOnWorld() {
        return super.isOnWorld();
    }

    @Override
    public void onAddToWorld(World world) {
        super.onAddToWorld(world);
    }

    @Override
    public void sync() {
        super.sync();
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {
        this.permanentSync = permanentSync;
    }

    @Override
    public boolean isPermanentSync() {
        return permanentSync;
    }
}
