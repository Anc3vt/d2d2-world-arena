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

import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.ISprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IScalable;
import com.ancevt.d2d2world.gameobject.area.AreaWater;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class FireWeapon extends Weapon {

    public FireWeapon() {
        super(createSprite());
        setMaxAmmunition(100);
    }


    @Contract(" -> new")
    public static @NotNull ISprite createSprite() {
        return new Sprite(
                MapkitManager.getInstance()
                        .getMapkit(BuiltInMapkit.NAME)
                        .getTextureAtlas("tileset.png")
                        .createTexture(0, 80, 32, 32)
        );
    }

    @Override
    public int getAttackTime() {
        return 5;
    }

    @Override
    public boolean shoot(@NotNull World world) {
        if (!super.shoot(world)) return false;
        Bullet bullet = getNextBullet(getOwner().getArmDegree());
        if (world.getGameObjectById(bullet.getGameObjectId()) == null) {
            bullet.setDamagingOwnerActor(getOwner());
            float deg = getOwner().getArmDegree();

            float[] toXY = RadialUtils.xySpeedOfDegree(deg);
            float distance = RadialUtils.distance(0, 0,
                    (getOwner().getWeaponX() + 60f) * getOwner().getDirection(),
                    getOwner().getWeaponY());

            bullet.setXY(getOwner().getX(), getOwner().getY());
            bullet.move(toXY[0] * distance, toXY[1] * distance);
            bullet.setDirection(getOwner().getDirection());

            if (RadialUtils.getDirection(deg) < 0) {
                bullet.setScaleY(-1f);
            }

            world.addGameObject(bullet, 4, false);
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class FireBullet extends Bullet implements IScalable {

        public FireBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
            super(mapkitItem, gameObjectId);
            addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);
        }

        private void this_addToStage(Event event) {
            removeEventListener(this, Event.ADD_TO_STAGE);
            TextureAtlas textureAtlas = getMapkitItem().getTextureAtlas();
            FramedSprite framedSprite = new FramedSprite(
                    textureAtlas.createTextures(getMapkitItem().getDataEntry().getString(DataKey.IDLE))
            );
            framedSprite.setFrame(0);
            framedSprite.setLoop(true);
            framedSprite.play();
            framedSprite.setXY(-framedSprite.getWidth() / 2, -framedSprite.getHeight() / 2 - 16f);
            add(framedSprite);
        }

        @Override
        public void onCollide(ICollision collideWith) {
            if(collideWith instanceof AreaWater && isOnWorld()) {
                getWorld().removeGameObject(this, false);
            }

        }

        @Override
        public void prepare() {

        }

        @Override
        public void destroy() {
            setSpeed(0);
        }

        @Override
        public void process() {
            if (isOnWorld()) {
                toAlpha(0.925f);
                if (getAlpha() <= 0.1f) {
                    getWorld().removeGameObject(this, false);
                }
            }

            float[] xy = RadialUtils.xySpeedOfDegree(getDegree());
            move(getSpeed() * xy[0], getSpeed() * xy[1] - 1);
            super.process();
        }
    }
}
