package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AutomaticWeapon extends Weapon {

    public AutomaticWeapon() {
        super(createSprite());
        setMaxAmmunition(50);
    }

    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 32, 32, 16)
        );
    }

    @Override
    public int getAttackTime() {
        return 10;
    }

    @Override
    public boolean shoot(@NotNull World world) {
        if (!super.shoot(world)) return false;

        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RadialUtils.xySpeedOfDegree(deg);
            float distance = RadialUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
            bullet.setXY(getOwner().getX(), getOwner().getY());
            bullet.move(toXY[0] * distance, toXY[1] * distance - 3);
            bullet.setDirection(getOwner().getDirection());
            world.addGameObject(bullet, 4, false);
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class AutomaticBullet extends Bullet {

        private boolean setToRemove;

        public AutomaticBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(AutomaticBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(AutomaticWeapon.class);
            Sprite sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setColor(Color.GRAY);
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void onAddToWorld(World world) {
            super.onAddToWorld(world);
            playSound("automatic.ogg");
        }

        @Override
        public void process() {
            float[] xy = RadialUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1]);
            super.process();
        }

        @Override
        public void prepare() {

        }

        @Override
        public void destroy() {
            setSpeed(0);
            setToRemove = true;
            setCollisionEnabled(false);

            if (hasParent()) {
                IDisplayObject displayObjectContainer = Particle.miniExplosion(5, Color.DARK_GRAY, 2f);
                displayObjectContainer.setScale(0.25f, 0.25f);
                getParent().add(displayObjectContainer, getX(), getY());
            }
        }

        @Override
        public void onEachFrame() {
            if (setToRemove && isOnWorld()) {
                toScale(0.1f, 0.1f);
                toAlpha(0.8f);
                moveY(-1);
                if (getAlpha() <= 0.1f) {
                    getWorld().removeGameObject(this, false);
                }
            }
            super.onEachFrame();
        }

        @Override
        public void setPermanentSync(boolean permanentSync) {

        }

        @Override
        public boolean isPermanentSync() {
            return false;
        }
    }
}
