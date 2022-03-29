package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ArrowWeapon extends Weapon {


    public ArrowWeapon() {
        super(createSprite());
        setMaxAmmunition(15);
        setAmmunition(1);
    }

    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getByName(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 128, 32, 32)
        );
    }

    @Override
    public int getAttackTime() {
        return 20;
    }

    @Override
    public void playShootSound() {
        getBulletMapkitItem().getMapkit().playSound("standard-bullet.ogg");
    }

    @Override
    public void playBulletDestroySound() {

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

    public static class ArrowBullet extends Bullet implements ITight {

        private boolean setToRemove;
        private int destroyTime = 300;
        private Sprite sprite;

        public ArrowBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(ArrowBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(PlasmaWeapon.class);
            sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void prepare() {

        }

        @Override
        public void destroy() {
            setSpeed(0);
            setToRemove = true;
            setDamagingOwnerActor(null);
            setDamagingPower(0);
            //setCollisionEnabled(true);
        }

        @Override
        public void process() {
            if (setToRemove && isOnWorld()) {
                destroyTime--;

                if(destroyTime <= 0) {
                    toAlpha(0.9f);
                }

                if (getAlpha() <= 0.1f) {
                    getWorld().removeGameObject(this, false);
                }
            } else {
                float[] xy = RotationUtils.xySpeedOfDegree(getDegree());
                move(getSpeed() * xy[0], getSpeed() * xy[1]);
            }
            super.process();
        }

        @Override
        public void setFloorOnly(boolean b) {

        }

        @Override
        public void onCollide(ICollision collideWith) {
            super.onCollide(collideWith);
        }

        @Override
        public boolean isFloorOnly() {
            return true;
        }

        @Override
        public void setPushable(boolean b) {

        }

        @Override
        public boolean isPushable() {
            return false;
        }
    }
}
