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

import com.ancevt.commons.Pair;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

abstract public class Weapon {

    private static final Map<Class<? extends Weapon>, Pair<Float, Float>> weaponXYOffsetMap = new HashMap<>() {{
        put(FireWeapon.class, Pair.of(2f, -11f));
    }};

    private final ISprite sprite;
    private final Mapkit mapkit;
    private Actor owner;
    private int ammunition;
    private int maxAmmunition;

    public Weapon(@NotNull ISprite sprite) {
        this.sprite = sprite;
        mapkit = MapkitManager.getInstance().getMapkit(BuiltInMapkit.NAME);
    }

    public static Pair<Float, Float> getXYOffset(Class<? extends Weapon> clazz) {
        return weaponXYOffsetMap.getOrDefault(clazz, Pair.of(2f, -11f));
    }

    public @NotNull MapkitItem getBulletMapkitItem() {
        return mapkit.getItemById("bullet_of_" + getClass().getSimpleName());
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public ISprite getSprite() {
        return sprite;
    }

    public void setMaxAmmunition(int maxAmmunition) {
        this.maxAmmunition = maxAmmunition;
    }

    public int getMaxAmmunition() {
        return maxAmmunition;
    }

    public boolean addAmmunition(int value) {
        return setAmmunition(getAmmunition() + value);
    }

    public boolean setAmmunition(int value) {
        var oldAmmunition = ammunition;

        ammunition = value;
        if (ammunition > maxAmmunition) {
            ammunition = maxAmmunition;
        } else if (ammunition <= 0) {
            this.ammunition = 0;
        }

        if (ammunition == oldAmmunition) return false;

        return true;
    }

    public int getAmmunition() {
        return ammunition;
    }

    public Bullet getNextBullet(float degree) {
        Bullet bullet = (Bullet) getBulletMapkitItem().createGameObject(IdGenerator.getInstance().getNewId());
        bullet.setDegree(degree);
        return bullet;
    }

    abstract public int getAttackTime();

    public boolean shoot(@NotNull World world) {
        if (ammunition < 0) {
            ammunition = 0;
            // TODO: play weapon empty sound
            return false;
        }

        ammunition--;
        return true;
    }

    public Actor getOwner() {
        return owner;
    }

    public void setOwner(@NotNull Actor owner) {
        this.owner = owner;
    }

    public static @NotNull Weapon createWeapon(String className) {
        try {
            return (Weapon) Class.forName(className).getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException |
                InstantiationException | IllegalAccessException |
                NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "ammunition=" + ammunition +
                ", maxAmmunition=" + maxAmmunition +
                '}';
    }

    abstract public static class Bullet extends Container implements
            ICollision,
            IDirectioned,
            ISpeedable,
            IDamaging,
            ISynchronized,
            ISonicSynchronized {

        private final MapkitItem mapkitItem;
        private final int gameObjectId;
        private Actor owner;
        private int direction;
        private int damagingPower;
        private float degree;
        private World world;
        private float collisionY;
        private float collisionX;
        private float collisionHeight;
        private float collisionWidth;
        private boolean collisionEnabled;
        private float speed;
        private boolean permanentSync;

        public Bullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            this.mapkitItem = mapkitItem;
            this.gameObjectId = gameObjectId;
            setCollisionEnabled(true);
        }

        @Override
        public MapkitItem getMapkitItem() {
            return mapkitItem;
        }

        @Override
        public void setWorld(World world) {
            this.world = world;
        }

        @Override
        public World getWorld() {
            return world;
        }

        @Override
        public void process() {
            if (isOnWorld()) {
                if (getX() < 0 || getX() > getWorld().getRoom().getWidth() ||
                        getY() < 0 || getY() > getWorld().getRoom().getHeight()) {
                    destroy();
                }
            }
        }

        @Property
        public int getOwnerGameObjectId() {
            if (owner == null) return 0;
            return owner.getGameObjectId();
        }

        @Override
        public void setDamagingPower(int damagingPower) {
            this.damagingPower = damagingPower;
        }

        @Override
        public int getDamagingPower() {
            return damagingPower;
        }

        abstract public void prepare();

        abstract public void destroy();

        @Override
        public int getGameObjectId() {
            return gameObjectId;
        }

        @Override
        public boolean isSavable() {
            return false;
        }

        @Override
        public void setDamagingOwnerActor(Actor actor) {
            owner = actor;
        }

        @Override
        public Actor getDamagingOwnerActor() {
            return owner;
        }

        @Override
        public void setDirection(int direction) {
            this.direction = direction;
            //setScaleX(direction);
        }

        @Override
        public int getDirection() {
            return direction;
        }

        @Property
        public void setDegree(float degree) {
            this.degree = degree;
            setRotation(degree);
        }

        @Property
        public float getDegree() {
            return degree;
        }

        @Override
        public void setCollisionEnabled(boolean value) {
            this.collisionEnabled = value;
        }

        @Override
        public boolean isCollisionEnabled() {
            return collisionEnabled;
        }

        @Override
        public void setCollisionWidth(float collisionWidth) {
            this.collisionWidth = collisionWidth;
        }

        @Override
        public float getCollisionWidth() {
            return collisionWidth;
        }

        @Override
        public void setCollisionHeight(float collisionHeight) {
            this.collisionHeight = collisionHeight;
        }

        @Override
        public float getCollisionHeight() {
            return collisionHeight;
        }

        @Override
        public void setCollisionX(float collisionX) {
            this.collisionX = collisionX;
        }

        @Override
        public float getCollisionX() {
            return collisionX;
        }

        @Override
        public void setCollisionY(float collisionY) {
            this.collisionY = collisionY;
        }

        @Override
        public float getCollisionY() {
            return collisionY;
        }

        @Override
        public void onCollide(ICollision collideWith) {

        }

        @Override
        public void setSpeed(float speed) {
            this.speed = speed;
        }

        @Override
        public float getSpeed() {
            return speed;
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
}
