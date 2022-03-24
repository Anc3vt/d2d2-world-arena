package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StandardWeapon extends Weapon {


    public StandardWeapon(@NotNull Actor owner) {
        super(MapkitManager.getInstance().getByName(CharacterMapkit.NAME).getItem("standard_bullet"), owner, getOrCreateDisplayObject());
    }

    private static MapkitItem getOrCreateMapkitItem() {
        return null;
    }

    @Contract(" -> new")
    private static @NotNull IDisplayObject getOrCreateDisplayObject() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getByName(CharacterMapkit.NAME)
                        .getTextureAtlas("bullets.png")
                        .createTexture(0, 0, 32, 32)
        );
    }

    @Override
    public int getAttackTime() {
        return 20;
    }

    @Override
    public void playShootSound() {

    }

    @Override
    public void playBulletDestroySound() {

    }

    @Override
    public void shoot(@NotNull World world) {
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();
            float[] toXY = RotationUtils.xySpeedOfDegree(deg);
            float distance = RotationUtils.distance(0, 0, getOwner().getWeaponX() * getOwner().getDirection(), getOwner().getWeaponY());
            bullet.setXY(getOwner().getX(), getOwner().getY());
            bullet.move(toXY[0] * distance, toXY[1] * distance + getOwner().getWeaponY());
            bullet.setDirection(getOwner().getDirection());
            world.addGameObject(bullet, 5, false);
        }
    }
}
