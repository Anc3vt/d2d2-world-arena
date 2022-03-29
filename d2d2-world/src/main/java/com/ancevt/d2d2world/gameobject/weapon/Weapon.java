/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.debug.DebugPanel;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

abstract public class Weapon {

    private final ISprite sprite;
    private final Mapkit mapkit;
    private Actor owner;
    private int ammunition;
    private int maxAmmunition;

    public Weapon(@NotNull ISprite sprite) {
        this.sprite = sprite;
        mapkit = MapkitManager.getInstance().getByName(BuiltInMapkit.NAME);
    }

    public @NotNull MapkitItem getBulletMapkitItem() {
        return mapkit.getItem("bullet_of_" + getClass().getSimpleName());
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
        final int oldAmmunition = ammunition;
        if(value == ammunition) return false;
        ammunition = value;
        if (ammunition > maxAmmunition) {
            ammunition = maxAmmunition;
        } else if (ammunition <= 0) {
            this.ammunition = 0;
        }

        if(oldAmmunition == ammunition) return false;
        return true;
    }

    public int getAmmunition() {
        return ammunition;
    }

    public Bullet getNextBullet(float degree) {
        Bullet bullet = (Bullet) getBulletMapkitItem().createGameObject(IdGenerator.INSTANCE.getNewId());
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
        DebugPanel.createIfEnabled("shoot", "amm " + ammunition);
        return true;
    }

    abstract public void playShootSound();

    abstract public void playBulletDestroySound();

    public Actor getOwner() {
        return owner;
    }

    public void setOwner(@NotNull Actor owner) {
        this.owner = owner;
    }

    public static Weapon createWeapon(String className) {
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

    abstract public static class Bullet extends DisplayObjectContainer implements
            ICollision,
            IDirectioned,
            ISpeedable,
            IDamaging,
            ISynchronized {

        private Actor owner;
        private int direction;
        private int damagingPower;
        private float degree;

        public Bullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            setMapkitItem(mapkitItem);
            setGameObjectId(gameObjectId);
            setCollisionEnabled(true);
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
    }
}
