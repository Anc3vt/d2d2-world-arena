package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.scene.Particle;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlasmaWeapon extends Weapon {

    public PlasmaWeapon() {
        super(getOrCreateDisplayObject());
        setMaxAmmunition(500);
        setAmmunition(getMaxAmmunition());
    }

    @Contract(" -> new")
    private static @NotNull IDisplayObject getOrCreateDisplayObject() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getByName(CharacterMapkit.NAME)
                        .getTextureAtlas("bullets.png")
                        .createTexture(0, 32, 32, 32)
        );
    }

    @Override
    public int getAttackTime() {
        return 5;
    }

    @Override
    public void shoot(@NotNull World world) {
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RotationUtils.xySpeedOfDegree(deg);
            float distance = RotationUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
            bullet.setXY(getOwner().getX(), getOwner().getY());
            bullet.move(toXY[0] * distance, toXY[1] * distance);
            bullet.setDirection(getOwner().getDirection());
            world.addGameObject(bullet, 5, false);
        }
    }

    @Override
    public void playShootSound() {
        getBulletMapkitItem().getMapkit().playSound("plasma.ogg");
    }

    @Override
    public void playBulletDestroySound() {

    }

    public static class PlasmaBullet extends Bullet {

        private Sprite sprite;
        private boolean setToRemove;

        public PlasmaBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(PlasmaBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(PlasmaWeapon.class);
            sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setColor(Color.of(0x00FFFF));
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void process() {
            float[] xy = RotationUtils.xySpeedOfDegree(getDegree());

            float random = (float) (Math.random() * 5 - 2.5f);

            move(getSpeed() * xy[0] + random, getSpeed() * xy[1] + random);

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

            IDisplayObject displayObjectContainer = Particle.miniExplosion(5, Color.of(0x00FFFF), 5f);
            displayObjectContainer.setScale(0.25f, 0.25f);
            getParent().add(displayObjectContainer, getX(), getY());
        }

        @Override
        public void onEachFrame() {
            if (setToRemove && isOnWorld()) {
                toScale(0.8f, 0.8f);
                toAlpha(0.8f);
                moveY(-1);
                rotate((float) (Math.random() * 30));
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
