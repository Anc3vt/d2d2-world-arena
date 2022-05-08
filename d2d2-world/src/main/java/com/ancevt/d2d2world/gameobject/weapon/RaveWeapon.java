/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.gameobject.IScalable;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RaveWeapon extends Weapon {

    public RaveWeapon() {
        super(createSprite());
        setMaxAmmunition(50);
    }

    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 144, 32, 16)
        ) {
            @Override
            public void onEachFrame() {
                setColor(Color.createVisibleRandomColor());
            }
        };
    }

    @Override
    public int getAttackTime() {
        return 14;
    }

    @Override
    public boolean shoot(@NotNull World world) {
        if (!super.shoot(world)) return false;

        for (int i = 0; i < 3; i++) {
            float deg = getOwner().getArmDegree();
            deg += ((i * 10f) - 10f) * Math.random();

            Bullet bullet = getNextBullet(deg);
            if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
                bullet.setDamagingOwnerActor(getOwner());
                bullet.setSpeed(bullet.getSpeed() + i);
                if (i == 0) ((RaveWeaponBullet) bullet).setPlaySound(true);
                float[] toXY = RadialUtils.xySpeedOfDegree(deg);
                float distance = RadialUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
                bullet.setXY(getOwner().getX(), getOwner().getY());
                bullet.move(toXY[0] * distance, toXY[1] * distance + getOwner().getWeaponY());
                bullet.setDirection(getOwner().getDirection());

                if (RadialUtils.getDirection(deg) < 0) {
                    bullet.setScaleY(-1f);
                }

                world.addGameObject(bullet, 4, false);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class RaveWeaponBullet extends Bullet implements IScalable {

        private boolean setToRemove;
        private boolean playSound;
        private int tact;

        public RaveWeaponBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(RaveWeaponBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
            setAlpha(0.0f);
        }

        @Property
        public void setPlaySound(boolean b) {
            playSound = b;
        }

        @Property
        public boolean isPlaySound() {
            return playSound;
        }

        private void this_addToStage(Event event) {
            removeEventListener(RaveWeapon.class);

            var a = getMapkitItem().getTextureAtlas();
            FramedSprite framedSprite = new FramedSprite(
                    a.createTextures(getMapkitItem().getDataEntry().getString(DataKey.IDLE))
            );
            framedSprite.setFrame(0);
            framedSprite.setLoop(true);
            framedSprite.play();
            framedSprite.setXY(-framedSprite.getWidth() / 2, -framedSprite.getHeight() / 2);
            add(framedSprite);

            framedSprite.setXY(-framedSprite.getWidth() / 2, -framedSprite.getHeight() / 2);
            add(framedSprite);
        }

        @Override
        public void onAddToWorld(World world) {
            super.onAddToWorld(world);
            if (playSound) {
                playSound("rave-weapon.ogg");
            }
        }

        @Override
        public void process() {
            tact++;
            float[] xy = RadialUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1]);

            if(tact == 2) setAlpha(1f);

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
                IDisplayObject displayObjectContainer = Particle.miniExplosion(2, Color.createRandomColor(), 10f);
                displayObjectContainer.setScale(0.25f, 0.25f);
                getParent().add(displayObjectContainer, getX(), getY());
            }
        }

        @Override
        public void onEachFrame() {
            if (setToRemove && isOnWorld()) {
                toScale(0.8f, 0.8f);
                toAlpha(0.5f);
                moveY(-1);
                rotate(45);
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
