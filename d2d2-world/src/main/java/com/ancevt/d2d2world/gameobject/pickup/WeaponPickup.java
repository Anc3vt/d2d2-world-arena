package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
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
        setScale(1f, 1f);
    }

    @Property
    public void setWeaponClass(String cls) {
        if (cls == null || cls.isBlank() || cls.equals("null")) return;

        try {
            System.out.println(cls);

            weaponClass = (Class<? extends Weapon>) Class.forName(cls);

            Method method = weaponClass.getMethod("createSprite");
            Sprite sprite = (Sprite) method.invoke(null);
            getImage().setTexture(sprite.getTexture());
            getImage().setXY(-getImage().getWidth() / 1.5f, -getImage().getHeight() / 2 + 1);

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Property
    public String getWeaponClass() {
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
    public void onPlayerActorPickUpPickup(PlayerActor playerActor) {
        try {
            Weapon weapon = weaponClass.getConstructor().newInstance();
            playerActor.addWeapon(weapon, getAmmunition());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Set<Class<? extends Weapon>> findAllSubclassesOfWeapon() {
        Reflections reflections = new Reflections(Weapon.class.getPackage().getName(), Scanners.SubTypes);
        return new HashSet<>(reflections.getSubTypesOf(Weapon.class));
    }
}
