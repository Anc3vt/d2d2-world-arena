package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RailWeapon extends Weapon {

    public RailWeapon() {
        super(createSprite());
        setMaxAmmunition(15);
    }

    @Contract(" -> new")
    public static @NotNull Sprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 48, 32, 16)
        );
    }

    @Override
    public int getAttackTime() {
        return 50;
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
            bullet.setXY(getOwner().getX(), getOwner().getY() - 3);
            bullet.move(toXY[0], toXY[1]);
            bullet.setDirection(getOwner().getDirection());
            bullet.setScaleY(getOwner().getDirection());
            world.addGameObject(bullet, 4, false);
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
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
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void onAddToWorld(World world) {
            super.onAddToWorld(world);
            getMapkitItem().getMapkit().playSound("rail.ogg");
        }

        @Override
        public void process() {
            float[] xy = RotationUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1]);

            if (!setToRemove) {
                Sprite sprite = new Sprite(getMapkitItem().getTexture());
                DisplayObjectContainer doc = new DisplayObjectContainer() {
                    @Override
                    public void onEachFrame() {
                        super.onEachFrame();
                        toAlpha(0.94f);
                        if (getAlpha() < 0.01f) removeFromParent();
                    }
                };
                if(getDamagingOwnerActor() instanceof PlayerActor playerActor) {
                    sprite.setColor(playerActor.getPlayerColor());
                }

                doc.add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
                doc.setRotation(getRotation());
                doc.setXY(getX(), getY());
                getWorld().getLayer(4).add(doc);
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
            if(getParent() != null) {
                getParent().add(displayObjectContainer, getX(), getY());
            }
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
