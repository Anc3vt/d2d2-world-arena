package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.gameobject.PlayerActor;
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
    }

    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 64, 32, 16)
        );
    }

    @Override
    public int getAttackTime() {
        return 100;
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
        private int destroyTime = 500;
        private Sprite sprite;

        public ArrowBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(ArrowBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
            setPushable(false);
            setFloorOnly(false);
        }

        private void this_addToStage(Event event) {
            removeEventListener(PlasmaWeapon.class);
            sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            if(getDamagingOwnerActor() instanceof PlayerActor playerActor) {
                sprite.setColor(playerActor.getPlayerColor());
            }
            add(sprite);
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
            setDamagingOwnerActor(null);
            setDamagingPower(0);
            //setCollisionEnabled(true);
        }

        @Override
        public void process() {
            if (setToRemove && isOnWorld()) {
                destroyTime--;

                if(destroyTime <= 0) {
                    toAlpha(0.99f);
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
        public void onCollide(ICollision collideWith) {
            super.onCollide(collideWith);
        }

    }
}
