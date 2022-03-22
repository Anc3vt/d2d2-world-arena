package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import org.jetbrains.annotations.NotNull;

public class StandardWeapon extends Weapon {


    public StandardWeapon(@NotNull MapkitItem bulletMapkitItem, @NotNull Actor owner) {
        super(bulletMapkitItem, owner, getOrCreateDisplayObject());
    }

    private static IDisplayObject getOrCreateDisplayObject() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getByName(CharacterMapkit.NAME)
                        .getTextureAtlas("bullets.png")
                        .createTexture(0, 0, 32, 32)
        );
    }

}
