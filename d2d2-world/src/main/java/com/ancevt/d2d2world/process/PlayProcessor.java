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
package com.ancevt.d2d2world.process;

import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.gameobject.area.AreaCollision;
import com.ancevt.d2d2world.gameobject.area.AreaDoorTeleport;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayProcessor {

    private static final float DEFAULT_GRAVITY = 1f;

    public static float MAX_VELOCITY_X = 20f;
    public static float MAX_VELOCITY_Y = 10f;

    private final World world;

    private float gravity;

    private int delay;
    private boolean enabled;

    private int tact;

    public PlayProcessor(World world) {
        this.world = world;
        gravity = DEFAULT_GRAVITY;
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
        if (!enabled) return;

        tact++;

        if (tact % 10 == 0) pushStates.clear();

        for (int i = 0; i < world.getGameObjectCount(); i++) {
            final IGameObject o1 = world.getGameObject(i);

            o1.process();

            if (o1 instanceof IActioned actioned) {
                actioned.getActionProgram().process();
            }

            if (o1 instanceof IGravitied g) {
                processGravity(g);
            }

            boolean collideWithFloor = false;

            for (int j = 0; j < world.getGameObjectCount(); j++) {
                final IGameObject o2 = world.getGameObject(j);

                if (o1 == o2) continue;

                if (o1 instanceof ICollision collision1 && o2 instanceof ICollision collision2) {
                    if (hitTest(collision1, collision2)) {
                        processCollisionsHits(collision1, collision2);
                        collideWithFloor = true;
                    }
                }
            }

            if (o1 instanceof IGravitied g && !collideWithFloor) {
                g.setFloor(null);
            }

            if (o1 instanceof Actor actor) {
                if (D2D2World.isServer() && actor.getY() > world.getRoom().getHeight() && actor.isAlive()) {
                    actor.setHealthBy(0, null, false);
                }
            }
        }

        if (D2D2World.isServer()) {
            pushStates.values().forEach(PushState::process);
        }
    }

    private void processCollisionsHits(@NotNull ICollision o1, @NotNull ICollision o2) {
        if (!o1.isCollisionEnabled() || !o2.isCollisionEnabled()) return;

        if (o1 instanceof IDestroyable destroyable && o2 instanceof IDamaging damaging) {
            processDamage(destroyable, damaging);
        }
        if (o1 instanceof ITight tight1 && o2 instanceof ITight tight2) {
            processTight(tight1, tight2);
        }
        if (o1 instanceof Actor actor && o2 instanceof AreaDoorTeleport areaDoorTeleport) {
            processDoorTeleport(actor, areaDoorTeleport);
        }
        if (o1 instanceof IHookable hookable && o2 instanceof AreaHook areaHook) {
            processHook(hookable, areaHook);
        }

        o1.onCollide(o2);
        o2.onCollide(o1);

        if (o2 instanceof Weapon.Bullet bullet && o1 instanceof ITight && o1 != bullet.getDamagingOwnerActor()) {
            bullet.destroy();
            if (o1 instanceof Weapon.Bullet bullet2) {
                bullet2.destroy();
            }
        }
    }

    private void processDamage(@NotNull IDestroyable o, @NotNull IDamaging damaging) {
        if (damaging.getDamagingOwnerActor() == o) return;
        int damagingPower = damaging.getDamagingPower();
        o.damage(damagingPower, damaging);
    }

    private void processHook(@NotNull IHookable o, AreaHook hook) {
        if (o.getHook() != null &&
                o.getY() + o.getCollisionY() > hook.getY() + hook.getCollisionY() / 2 &&
                o.getVelocityY() > 0) {

            o.setHook(hook);
        }
    }

    private void processTight(@NotNull ITight o1, @NotNull ITight o2) {
        if (!(o1 instanceof IMovable)) return;

        if (!o1.isPushable()) return;

        if (o2 instanceof Weapon.Bullet bullet) {
            if (bullet.getOwnerGameObjectId() == o1.getGameObjectId()) return;
        }

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

        boolean wallHitTest = false;

        if (!(o2 instanceof Weapon.Bullet) /*&& !(o2 instanceof PlayerActor)*/) {
            if (checkWalls && cx1 < x2 && y1 + h1 > y2 + 8) {
                o1.setX(x2 - w1 - tx1 - 1);
                getPushState(o1).pushFromRight().tightFromRight = o2;
                wallHitTest = true;
            }
            if (checkWalls && cx1 > x2 + w2 && y1 + h1 > y2 + 8) {
                o1.setX(x2 + w2 - tx1 + 1);
                getPushState(o1).pushFromLeft().tightFromLeft = o2;
                wallHitTest = true;
            }
        }

        if (o1 instanceof IGravitied g) {
            if (wallHitTest) {
                g.setMovingSpeedX(0);
            }
            boolean floorUnderObject = cy1 < cy2 && y1 + h1 < y2 + 11; // 11

            if (floorUnderObject) {
                if ((o2 instanceof IMovable moveable && moveable.getMovingSpeedY() < 5) || g.getVelocityY() > 0) {
                    o1.setY(y2 - h1 - ty1);
                    setFloorTo(g, o2);
                }
                getPushState(o1).pushFromBottom().tightBelow = o2;
            } else if (checkWalls && cy1 > cy2 && y1 + 8 > y2 + h2 && cx1 > x2 && cx1 < x2 + w2) {
                o1.setY(y2 + h2 - ty1);
                g.setFloor(null);
                g.setVelocityY(2);
                getPushState(o1).pushFromTop().tightAbove = o2;
            }
        }
    }

    private static void setFloorTo(@NotNull IGravitied target, ICollision floor) {
        if (target.getFloor() == floor) return;
        target.setVelocityY(0);
        target.setFloor(floor);
    }

    private static boolean hitTest(@NotNull ICollision o1, @NotNull ICollision o2) {
        float x1 = o1.getX() + o1.getCollisionX() + 1;
        float y1 = o1.getY() + o1.getCollisionY() + 1;
        float w1 = o1.getCollisionWidth() + 1;
        float h1 = o1.getCollisionHeight() + 1;

        float x2 = o2.getX() + o2.getCollisionX();
        float y2 = o2.getY() + o2.getCollisionY();
        float w2 = o2.getCollisionWidth();
        float h2 = o2.getCollisionHeight();

        return x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2;
    }

    private void processGravity(IGravitied o) {
        if (!D2D2World.isServer()) return;

        float velX = o.getVelocityX();
        if (Math.abs(velX) > MAX_VELOCITY_X) o.setVelocityX(velX * .05f);

        if (o.isGravityEnabled() && o.getFloor() == null) {
            float fallSpeed = o.getWeight() * gravity;
            o.setVelocityY(o.getVelocityY() + fallSpeed);

            if (o.getVelocityY() > MAX_VELOCITY_Y) o.setVelocityY(MAX_VELOCITY_Y);
        }

        o.setVelocityX(Math.abs(velX) < 0.1f ? 0 : velX * 0.75f);
        o.move(o.getVelocityX(), o.getVelocityY());
    }

    private void processDoorTeleport(Actor actor, AreaDoorTeleport area) {
        if (world.isSwitchingRoomsNow()) return;

        String targetRoomId = area.getTargetRoomId();
        float targetX = area.getTargetX();
        float targetY = area.getTargetY();

        world.switchRoom(targetRoomId, targetX, targetY);
    }

    // Push states:
    private final Map<ITight, PushState> pushStates = new HashMap<>();

    private PushState getPushState(ITight tight) {
        var ps = pushStates.get(tight);
        if (ps == null) {
            ps = new PushState(world);
            ps.tight = tight;
            pushStates.put(tight, ps);
        }
        return ps;
    }

    public void reset() {
        pushStates.clear();
    }

    private static class PushState {

        ITight tight;
        ITight tightAbove;
        ITight tightBelow;
        ITight tightFromLeft;
        ITight tightFromRight;
        int pushFromLeft;
        int pushFromRight;
        int pushFromTop;
        int pushFromBottom;

        public PushState(World world) {
        }

        PushState pushFromLeft() {
            pushFromLeft++;
            return this;
        }

        PushState pushFromRight() {
            pushFromRight++;
            return this;
        }

        PushState pushFromBottom() {
            pushFromBottom = 2;
            return this;
        }

        PushState pushFromTop() {
            pushFromTop = 2;
            return this;
        }

        void process() {
            if (pushFromBottom > 1 && pushFromTop > 1) {
                if (tight instanceof Actor actor
                        && (tightAbove instanceof IPlatform || tightAbove instanceof AreaCollision)
                        && (tightBelow instanceof IPlatform || tightBelow instanceof AreaCollision)
                ) {
                    actor.setHealthBy(0, null, false);
                }
            }
            if (pushFromLeft > 0 && pushFromRight > 0) {
                if (tight instanceof Actor actor
                        && (tightFromLeft instanceof IPlatform || tightFromLeft instanceof AreaCollision)
                        && (tightFromRight instanceof IPlatform || tightFromRight instanceof AreaCollision)
                        && !(tightFromLeft instanceof AreaCollision && tightFromRight instanceof AreaCollision)
                ) {
                    actor.setHealthBy(0, null, false);
                }
            }

            if (pushFromBottom > 0) pushFromBottom--;
            if (pushFromTop > 0) pushFromTop--;
            if (pushFromLeft > 0) pushFromLeft--;
            if (pushFromRight > 0) pushFromRight--;
        }
    }
}































