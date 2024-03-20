/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ArrowWeapon extends Weapon {


    public ArrowWeapon() {
        super(createSprite());
        setMaxAmmunition(20);
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
    public boolean shoot(@NotNull World world) {
        if (!super.shoot(world)) return false;
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RadialUtils.xySpeedOfDegree(deg);
            float distance = RadialUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
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
        private boolean floorOnly;
        private boolean pushable;
        private float oldX;
        private float oldY;

        public ArrowBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);
            setPushable(false);
            setFloorOnly(false);
        }

        private void this_addToStage(Event event) {
            removeEventListener(this, Event.ADD_TO_STAGE);
            sprite = new Sprite(getMapkitItem().getTexture());
            sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
            add(sprite);
        }

        @Override
        public void onAddToWorld(World world) {
            super.onAddToWorld(world);
            playSound("arrow-1.ogg");
        }

        @Override
        public void prepare() {

        }

        @Override
        public void destroy() {
            if (setToRemove) return;

            setSpeed(0);
            setToRemove = true;
            setDamagingOwnerActor(null);
            setDamagingPower(0);
            sprite.setTexture(getMapkitItem().getTexture(AnimationKey.DEATH, 0));
            playSound("arrow-2.ogg");

            setXY(oldX, oldY);

            if (hasParent()) {
                IDisplayObject Container = Particle.miniExplosion(10, Color.GRAY, 5f);
                Container.setScale(0.25f, 0.25f);
                getParent().add(Container, getX(), getY());
            }
        }

        @Override
        public void process() {
            if (setToRemove && isOnWorld()) {
                destroyTime--;

                if (destroyTime <= 0) {
                    toAlpha(0.99f);
                }

                if (getAlpha() <= 0.1f) {
                    getWorld().removeGameObject(this, false);
                }
            } else {
                oldX = getX();
                oldY = getY();

                float[] xy = RadialUtils.xySpeedOfDegree(getDegree());
                move(getSpeed() * xy[0], getSpeed() * xy[1]);
            }
            super.process();
        }

        @Override
        public void onCollide(ICollision collideWith) {
            super.onCollide(collideWith);
            if (!setToRemove && collideWith instanceof PlayerActor playerActor &&
                    getOwnerGameObjectId() != playerActor.getGameObjectId()) {
                Async.runLater(100, TimeUnit.MILLISECONDS, () -> {
                    if (isOnWorld()) getWorld().removeGameObject(this, false);
                });
            }
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
            pushable = b;
        }

        @Override
        public boolean isPushable() {
            return pushable;
        }
    }
}
