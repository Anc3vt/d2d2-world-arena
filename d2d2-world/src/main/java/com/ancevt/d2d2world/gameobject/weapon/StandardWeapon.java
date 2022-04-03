package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StandardWeapon extends Weapon {


    public StandardWeapon() {
        super(createSprite());
        setMaxAmmunition(100);
    }

    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 0, 32, 16)
        );
    }

    @Override
    public int getAttackTime() {
        return 40;
    }

    @Override
    public boolean shoot(@NotNull World world) {
        if(!super.shoot(world)) return false;
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RotationUtils.xySpeedOfDegree(deg);
            float distance = RotationUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
            bullet.setXY(getOwner().getX(), getOwner().getY());
            bullet.move(toXY[0] * distance, toXY[1] * distance + getOwner().getWeaponY());
            bullet.setDirection(getOwner().getDirection());
            world.addGameObject(bullet, 4, false);
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class StandardBullet extends Bullet implements ITight {

        private boolean setToRemove;
        private boolean floorOnly;
        private boolean pushable;

        public StandardBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(StandardBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(StandardBullet.class);
            var a = getMapkitItem().getTextureAtlas();
            FramedSprite framedSprite = new FramedSprite(
                    a.createTextures(getMapkitItem().getDataEntry().getString(DataKey.IDLE))
            );
            if(getDamagingOwnerActor() instanceof PlayerActor playerActor) {
                framedSprite.setColor(playerActor.getPlayerColor());
            }

            framedSprite.setFrame(0);
            framedSprite.setLoop(true);
            framedSprite.play();
            framedSprite.setXY(-framedSprite.getWidth() / 2, -framedSprite.getHeight() / 2);
            add(framedSprite);
        }

        @Override
        public void onAddToWorld(World world) {
            super.onAddToWorld(world);
            getMapkitItem().getMapkit().playSound("standard-bullet.ogg");
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

            float[] xy = RotationUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1]);
            super.process();
        }

        @Override
        public void setFloorOnly(boolean b) {
            this.floorOnly = b;
        }

        @Override
        public boolean isFloorOnly() {
            return floorOnly;
        }

        @Override
        public void setPushable(boolean b) {
            this.pushable = b;
        }

        @Override
        public boolean isPushable() {
            return pushable;
        }
    }
}
