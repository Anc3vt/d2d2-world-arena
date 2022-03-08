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
package ru.ancevt.d2d2world.process;

import ru.ancevt.d2d2world.gameobject.Actor;
import ru.ancevt.d2d2world.gameobject.IActioned;
import ru.ancevt.d2d2world.gameobject.ICollision;
import ru.ancevt.d2d2world.gameobject.IDamaging;
import ru.ancevt.d2d2world.gameobject.IDestroyable;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.IGravitied;
import ru.ancevt.d2d2world.gameobject.IHookable;
import ru.ancevt.d2d2world.gameobject.IMovable;
import ru.ancevt.d2d2world.gameobject.ITight;
import ru.ancevt.d2d2world.gameobject.area.AreaDoorTeleport;
import ru.ancevt.d2d2world.gameobject.area.AreaHook;
import ru.ancevt.d2d2world.process.action.ActionProcessor;
import ru.ancevt.d2d2world.world.World;

public class PlayProcessor {

    private static final float DEFAULT_GRAVITY = 1f;
    private static final int DEFAULT_SPEED = 60;

    public static float MAX_VELOCITY_X = 20f;
    public static float MAX_VELOCITY_Y = 10f;

    private final World world;

    private float gravity;

    private int delay;
    private boolean enabled;

    public PlayProcessor(World world) {
        this.world = world;
        gravity = DEFAULT_GRAVITY;
        setSpeed(DEFAULT_SPEED);
        setEnabled(true);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getGravity() {
        return gravity;
    }

    public World getWorld() {
        return world;
    }

    public final synchronized void process() {
        if(!enabled) return;

        IGravitied gravitied;

        for (int i = 0; i < world.getGameObjectCount(); i++) {
            final IGameObject o1 = world.getGameObject(i);

            if (o1 instanceof IActioned actioned) {
                processAction(actioned);
            }

            if (o1 instanceof IGravitied) {
                gravitied = (IGravitied) o1;
                gravitied.setFloor(null);
                processGravity(gravitied);
            }

            for (int j = 0; j < world.getGameObjectCount(); j++) {
                final IGameObject o2 = world.getGameObject(j);

                if (o1 == o2) continue;

                if (o1 instanceof ICollision collision1 && o2 instanceof ICollision collision2) {
                    if (hitTest(collision1, collision2)) {
                        processCollisionsHits(collision1, collision2);
                    }
                }

            }

            o1.process();
        }
    }

    private void processCollisionsHits(ICollision o1, ICollision o2) {
        if (!o1.isCollisionEnabled() || !o2.isCollisionEnabled()) return;

        o1.onCollide(o2);
        o2.onCollide(o1);

        if (o1 instanceof ITight tight1 && o2 instanceof ITight tight2)
            processTight(tight1, tight2);


        if (o1 instanceof Actor actor && o2 instanceof AreaDoorTeleport areaDoorTeleport)
            processDoorTeleport(actor, areaDoorTeleport);


        if (o1 instanceof IHookable hookable && o2 instanceof AreaHook areaHook)
            processHook(hookable, areaHook);


        if (o1 instanceof IDestroyable destroyable && o2 instanceof IDamaging damaging)
            processDamage(destroyable, damaging);

    }

    private void processDamage(IDestroyable o, IDamaging damaging) {
        if (damaging.getDamagingOwnerActor() == o) return;

        int damagingPower = damaging.getDamagingPower();

        o.addHealth(-damagingPower);
    }

    private void processHook(IHookable o, AreaHook hook) {
        if (o.getHook() != null &&
                o.getY() + o.getCollisionY() > hook.getY() + hook.getCollisionY() / 2 &&
                o.getVelocityY() > 0) {

            o.setHook(hook);
        }
    }

    private void processTight(ITight o1, ITight o2) {
        if (!(o1 instanceof IMovable)) return;

        if (!o1.isPushable()) return;

        float tx1 = o1.getCollisionX();
        float ty1 = o1.getCollisionY();

        float x1 = o1.getX() + o1.getCollisionX();
        float y1 = o1.getY() + o1.getCollisionY();
        float w1 = o1.getCollisionWidth();
        float h1 = o1.getCollisionHeight();
        float cx1 = x1 + (w1 / 2);
        float cy1 = y1 + (h1 / 2);

        float x2 = o2.getX() + o2.getCollisionX();
        float y2 = o2.getY() + o2.getCollisionY();
        float w2 = o2.getCollisionWidth();
        float h2 = o2.getCollisionHeight();

        float cy2 = y2 + (h2 / 2);

        final boolean checkWalls = !o2.isFloorOnly();

        if (checkWalls && cx1 < x2 && y1 + h1 > y2 + 8) {
            o1.setX(x2 - w1 - tx1);
        } else if (checkWalls && cx1 > x2 + w2 && y1 + h1 > y2 + 8) {
            o1.setX(x2 + w2 - tx1);
        }

        if (o1 instanceof IGravitied gravitied) {
            if (cy1 < cy2 && y1 + h1 < y2 + 11) {

                if (gravitied.getVelocityY() >= 0) {
                    o1.setY(y2 - h1 - ty1);
                    setFloorTo(gravitied, o2);
                }

            } else if (checkWalls && cy1 > cy2 && y1 + 8 > y2 + h2 && cx1 > x2 && cx1 < x2 + w2) {
                o1.setY(y2 + h2 - ty1);

                gravitied.setVelocityY(0);
            }
        }
    }

    private static void setFloorTo(IGravitied target, ICollision floor) {
        target.setVelocityY(0);
        target.setFloor(floor);
    }

    private static boolean hitTest(ICollision o1, ICollision o2) {
        float x1 = o1.getX() + o1.getCollisionX();
        float y1 = o1.getY() + o1.getCollisionY();
        float w1 = o1.getCollisionWidth();
        float h1 = o1.getCollisionHeight();

        float x2 = o2.getX() + o2.getCollisionX();
        float y2 = o2.getY() + o2.getCollisionY();
        float w2 = o2.getCollisionWidth();
        float h2 = o2.getCollisionHeight();

        return x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2;
    }

    private void processGravity(IGravitied o) {
        if (o.isGravityEnabled()) {
            float fallSpeed = o.getWeight() * gravity;
            o.setVelocityY(o.getVelocityY() + fallSpeed);

            if (o.getVelocityY() > MAX_VELOCITY_Y) o.setVelocityY(MAX_VELOCITY_Y);

            float velX = o.getVelocityX();

            if (Math.abs(velX) > MAX_VELOCITY_X) o.setVelocityX(velX * .05f);

            o.setVelocityX(Math.abs(velX) < 0.1f ? 0 : velX * 0.75f);
            o.move(o.getVelocityX(), o.getVelocityY());
        }
    }

    private void processDoorTeleport(Actor actor, AreaDoorTeleport area) {
        if (getWorld().isSwitchingRoomsNow()) return;

        String targetRoomId = area.getTargetRoomId();
        float targetX = area.getTargetX();
        float targetY = area.getTargetY();

        getWorld().switchRoom(targetRoomId, targetX, targetY);
    }

    private void processAction(final IActioned o) {
        ActionProcessor.process(o);
    }

    public final void setSpeed(int value) {
        delay = 1000 / value;
    }

    public final int getSpeed() {
        return 1000 / delay;
    }

}































