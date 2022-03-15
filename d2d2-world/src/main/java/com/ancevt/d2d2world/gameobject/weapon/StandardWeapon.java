package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class StandardWeapon extends Weapon{

    public StandardWeapon(@NotNull MapkitItem bulletMapkitItem, @NotNull Actor owner) {
        super(bulletMapkitItem, owner);
    }

}
