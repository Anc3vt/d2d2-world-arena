package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;

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

            if (Math.random() < 0.01) {
                createBubble().setXY(collideWith.getX(), collideWith.getY());
            }

            if (g.getY() < getY()) {
                var p = Particle.water((int) (5 * g.getVelocityY()), Color.WHITE, g.getVelocityY());
                p.setScale(0.5f, 0.5f);
                getParent().add(p, g.getX(), getY() - 16);
                if(splashSoundTime == 0) {
                    BuiltInMapkit.getInstance().playSound("splash.ogg");
                    splashSoundTime = SPLASH_SOUND_TIME;
                }
            }
        }
        if (collideWith instanceof ISpeedable s) {
            if (s.getSpeed() > 2.5f) s.setSpeed(2.5f);
        }
        if (collideWith instanceof Actor a) {

            if(a.getY() + a.getCollisionY() > getY()) {
                a.underWater(this);
            } else {
                a.resetUnderWater();
            }

            if (a.getController().isA()) {
                a.setVelocityY(a.getVelocityY() - 0.5f);

                Integer tact = actorTacts.computeIfAbsent(a, k -> 0);
                tact++;
                if(tact < 25) {
                    a.setAnimation(AnimationKey.JUMP);
                } else {
                    a.setAnimation(AnimationKey.FALL);
                }

                actorTacts.put(a, tact);

                if(tact > 50) {
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

        if(splashSoundTime > 0) {
            splashSoundTime --;
        }
    }

    private Bubble createBubble() {
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
            super(D2D2World.getWaterBubbleTexture());
        }
    }
}
