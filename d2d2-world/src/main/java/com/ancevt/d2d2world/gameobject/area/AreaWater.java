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
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.D2D2WorldAssets;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IGravitational;
import com.ancevt.d2d2world.gameobject.ISonicSynchronized;
import com.ancevt.d2d2world.gameobject.ISpeedable;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AreaWater extends Area implements IDamaging {

    public static final Color FILL_COLOR = Color.DARK_BLUE;
    private static final Color STROKE_COLOR = Color.ORANGE;
    private static final int SPLASH_SOUND_TIME = 50;

    private int splashSoundTime;

    private Map<IGameObject, Integer> actorTacts;
    private int damagingPower;

    public AreaWater(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        actorTacts = new HashMap<>();

        setTextVisible(true);
        setText("water");
        setBorderColor(STROKE_COLOR);
        setFillColor(FILL_COLOR);
    }

    @Override
    public void onCollide(ICollision collideWith) {

        if (collideWith instanceof IGravitational g) {
            g.setVelocity(g.getVelocityX() * 0.9f, g.getVelocityY() * 0.9f);
        }

        if (collideWith instanceof Weapon.Bullet || collideWith instanceof Actor) {
            if (collideWith.getY() < getY()) {
                float vel = 4f;

                var p = Particle.water(5, Color.WHITE, vel);
                p.setScale(0.5f, 0.5f);
                getWorld().add(p, collideWith.getX(), getY());
                if (splashSoundTime == 0) {
                    ISonicSynchronized sonicSynchronized = (ISonicSynchronized) collideWith;
                    sonicSynchronized.playSound("splash.ogg");
                    splashSoundTime = SPLASH_SOUND_TIME;
                }
            }
        }

        if (collideWith instanceof ISpeedable s && s.getY() > getY()) {
            if (s.getSpeed() > 5f) s.setSpeed(5f);
        }
        if (collideWith instanceof Actor a && a.getY() > getY()) {


            a.setVelocityX(a.getVelocityX() * 1.085f);

            if (a.getY() + a.getCollisionY() > getY() + 16) {
                boolean noAirLeft = a.underWater(this);
                if (noAirLeft) {
                    createBubble().setXY(collideWith.getX(), collideWith.getY() - 16);
                }
            } else {
                a.resetUnderWater();
            }

            if (a.getController().isA()) {
                a.setVelocityY(a.getVelocityY() - 0.35f);

                Integer tact = actorTacts.computeIfAbsent(a, k -> 0);
                tact++;
                if (tact < 25) {
                    a.setAnimation(AnimationKey.JUMP);
                } else {
                    a.setAnimation(AnimationKey.FALL);
                }

                actorTacts.put(a, tact);

                if (tact > 50) {
                    actorTacts.put(a, 0);
                }
            }
        }
    }

    @Override
    public void process() {
        super.process();
        if (!isOnWorld()) return;

        if (Math.random() < 0.005) {
            createBubble();
        }

        if (splashSoundTime > 0) {
            splashSoundTime--;
        }
    }

    private @NotNull Bubble createBubble() {
        Bubble bubble = new Bubble() {

            private int tact;

            @Override
            public void onEachFrame() {
                tact++;
                if (tact == 20) {
                    moveX(-1);
                }
                if (tact == 40) {
                    moveX(1);
                    tact = 0;
                }

                moveY(-0.5f);

                if (getY() < AreaWater.this.getY()) {
                    removeFromParent();
                }
            }
        };
        float scale = (float) Math.random();
        bubble.setScale(scale, scale);
        bubble.setAlpha((float) (Math.random() * 0.5f));
        bubble.setXY(getX() + (float) (Math.random() * getWidth()), getY() + getHeight());
        getParent().add(bubble);
        return bubble;
    }

    @Override
    public void setDamagingPower(int damagingPower) {
        this.damagingPower = damagingPower;
    }

    @Override
    public int getDamagingPower() {
        return damagingPower;
    }

    @Override
    public void setDamagingOwnerActor(Actor actor) {

    }

    @Override
    public Actor getDamagingOwnerActor() {
        return null;
    }

    private static class Bubble extends Sprite {
        public Bubble() {
            super(D2D2WorldAssets.getWaterBubbleTexture());
        }
    }
}
