package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
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

public class RailWeapon extends Weapon {

    public RailWeapon() {
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
                        .createTexture(0, 96, 32, 32)
        );
    }

    @Override
    public int getAttackTime() {
        return 50;
    }

    @Override
    public void shoot(@NotNull World world) {
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RotationUtils.xySpeedOfDegree(deg);
            float distance = RotationUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
            bullet.setXY(getOwner().getX(), getOwner().getY() - 5);
            bullet.move(toXY[0] * distance, toXY[1] * distance);
            bullet.setDirection(getOwner().getDirection());
            world.addGameObject(bullet, 5, false);
        }
    }

    @Override
    public void playShootSound() {
        getBulletMapkitItem().getMapkit().playSound("rail.ogg");
    }

    @Override
    public void playBulletDestroySound() {

    }

    public static class RailBullet extends Bullet {

        private boolean setToRemove;

        public RailBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(RailBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(RailWeapon.class);
            Sprite sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setColor(Color.LIGHT_GRAY);
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void process() {
            float[] xy = RotationUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1]);

            if (!setToRemove) {

                Sprite sprite = new Sprite(getMapkitItem().getTexture());
                DisplayObjectContainer doc = new DisplayObjectContainer(){
                    @Override
                    public void onEachFrame() {
                        super.onEachFrame();
                        toAlpha(0.94f);
                        if (getAlpha() < 0.01f) removeFromParent();
                    }
                };
                doc.add(sprite, -sprite.getWidth() / 2, - sprite.getHeight() / 2);
                doc.setRotation(getRotation());
                doc.setXY(getX(), getY());

                //sprite.setRotation(getRotation());
                //sprite.setScale(getScaleX(), getScaleY());

//                float deg = getDamagingOwnerActor().getArmDegree();
//                float[] toXY = RotationUtils.xySpeedOfDegree(deg);
//                float distance = RotationUtils.distance(0, 0, getDamagingOwnerActor().getWeaponX() * getDamagingOwnerActor().getDirection(),
//                      getDamagingOwnerActor().getWeaponY());
//                sprite.move(toXY[0] * distance, toXY[1] * distance + getDamagingOwnerActor().getWeaponY());



                getWorld().add(doc);
            }


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

            IDisplayObject displayObjectContainer = Particle.miniExplosion(5, Color.WHITE, 2f);
            displayObjectContainer.setScale(0.25f, 0.25f);
            getParent().add(displayObjectContainer, getX(), getY());
        }

        @Override
        public void onEachFrame() {
            if (setToRemove && isOnWorld()) {
                toScale(0.8f, 0.8f);
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
