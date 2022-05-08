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
package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.weapon.PlasmaWeapon;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class WeaponPickup extends Pickup {

    public static final String IDLE_COORDS = "0,0,32,32";

    private Class<? extends Weapon> weaponClass;
    private int ammunition;

    public WeaponPickup(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setWeaponClassname(PlasmaWeapon.class.getName());
    }

    @Property
    public void setWeaponClassname(String cls) {
        if (cls == null || cls.isBlank() || cls.equals("null")) return;

        // if class name without package
        if (!cls.startsWith(Weapon.class.getName().substring(0, 5))) {
            cls = Weapon.class.getPackageName() + "." + cls;
        }

        try {
            weaponClass = (Class<? extends Weapon>) Class.forName(cls);
            Method method = weaponClass.getMethod("createSprite");
            Sprite sprite = (Sprite) method.invoke(null);
            getImage().setTexture(sprite.getTexture());
            getImage().setXY(-getImage().getWidth() / 3, -getImage().getHeight() / 2);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playPickUpSound() {
        playSound("weapon-pickup.ogg");
    }

    @Property
    public String getWeaponClassname() {
        return weaponClass != null ? weaponClass.getName() : null;
    }

    @Property
    public void setAmmunition(int count) {
        this.ammunition = count;
    }

    @Property
    public int getAmmunition() {
        return ammunition;
    }

    @Override
    public boolean onPlayerActorPickUpPickup(@NotNull PlayerActor playerActor) {
        return playerActor.addWeapon(weaponClass, getAmmunition());
    }

    public Set<Class<? extends Weapon>> findAllSubclassesOfWeapon() {
        Reflections reflections = new Reflections(Weapon.class.getPackage().getName(), Scanners.SubTypes);
        return new HashSet<>(reflections.getSubTypesOf(Weapon.class));
    }

    @Override
    public String toString() {
        return "WeaponPickup{" +
                "weaponClass=" + weaponClass +
                ", ammunition=" + ammunition +
                ", collisionEnabled=" + isCollisionEnabled() +
                '}';
    }
}
