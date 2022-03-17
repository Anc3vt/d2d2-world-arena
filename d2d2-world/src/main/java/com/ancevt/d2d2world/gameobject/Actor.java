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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.control.Controller;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.scene.Particle;
import com.ancevt.d2d2world.ui.HealthBar;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;

abstract public class Actor extends Animated implements ISynchronized,
        IProcessable,
        IDirectioned,
        IMovable,
        IAnimated,
        IDestroyable,
        ITight,
        IResettable,
        IGravitied,
        IControllable,
        ISpeedable {

    private static final int ATTACK_TIME = 20;
    private static final int JUMP_TIME = 4;
    private static final int DAMAGING_TIME = 30;

    private int attackTime;
    private int jumpTime;
    private int damagingTime;
    private boolean onJump;

    private float startX, startY, movingSpeedX, movingSpeedY;
    private float speed;
    private int health, maxHealth;
    private boolean collisionEnabled;
    private float collisionX, collisionY, collisionWidth, collisionHeight;
    private float weight;
    private ICollision floor;
    private float velocityX, velocityY;
    private float jumpPower;
    private Controller controller;

    protected BitmapText bitmapTextDebug;
    private boolean fallEnabled;
    private float weaponLocationX, weaponLocationY;
    private boolean alive;
    private Weapon weapon;
    private boolean pushable;
    private World world;
    private final HealthBar healthBar;

    public Actor(MapkitItem mapkitItem, final int gameObjectId) {
        super(mapkitItem, gameObjectId);
        healthBar = new HealthBar();
        add(healthBar, -healthBar.getWidth() / 2, -24);
        setPushable(true);
        setGravityEnabled(true);
        setAlive(true);
        setCollisionEnabled(true);
        setAnimation(AnimationKey.IDLE);
        setDirection(Direction.RIGHT);
        setController(new Controller());
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public final void debug(final Object o) {
        if (o == null && bitmapTextDebug != null && bitmapTextDebug.hasParent()) {
            bitmapTextDebug.getParent().remove(bitmapTextDebug);
            bitmapTextDebug = null;
        }

        if (bitmapTextDebug == null) {
            bitmapTextDebug = new BitmapText() {
                @Override
                public void onEachFrame() {
                    getParent().add(this);
                    super.onEachFrame();
                }
            };
            bitmapTextDebug.setBounds(100, 30);
        }

        bitmapTextDebug.setText(o != null ? o.toString() : null);
        add(bitmapTextDebug);
        bitmapTextDebug.setScale(0.30f, 0.30f);
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();
        if (damagingTime > 0) {
            damagingTime--;
            setAlpha((damagingTime / 2) % 2 == 0 ? 1.0f : 0.0f);
        }
    }

    public final void debug(
            final Object o,
            final float offsetX,
            final float offsetY) {

        debug(o);
        if (bitmapTextDebug != null && bitmapTextDebug.hasParent())
            bitmapTextDebug.setXY(offsetX, offsetY);
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public void setStartX(float x) {
        startX = x;
    }

    @Override
    public void setStartY(float y) {
        startY = y;
    }

    @Override
    public void setStartXY(float x, float y) {
        setStartX(x);
        setStartY(y);
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getStartX() {
        return startX;
    }

    @Override
    public float getStartY() {
        return startY;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setMaxHealth(int health) {
        this.maxHealth = health;
        healthBar.setMaxValue(health);
        if (isOnWorld()) getWorld().getSyncDataAggregator().maxHealth(this);
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setHealth(int health) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;
        this.health = health;

        healthBar.setValue(health);

        if(health < oldHealth) damagingTime = DAMAGING_TIME;

        if (health <= 0 && isAlive()) death(null);

        if (isOnWorld()) getWorld().getSyncDataAggregator().health(this, null);
    }

    @Override
    public void setHealthBy(int health, IDamaging damaging) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth && isOnWorld()) health = maxHealth;
        this.health = health;

        if(health < oldHealth) damagingTime = DAMAGING_TIME;

        healthBar.setValue(health);

        if (health <= 0 && isAlive()) death(damaging);

        if (isOnWorld()) {
            getWorld().getSyncDataAggregator().health(this, damaging);
        }
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void repair() {
        setHealth(getMaxHealth());
        setAlive(true);

        if (isOnWorld()) getWorld().getSyncDataAggregator().repair(this);
    }

    @Override
    public void changeHealth(int toHealth, IDamaging damaging) {
        if (toHealth < 0) {
            if (getHealth() > -toHealth)
                getMapkitItem().playSound(SoundKey.DAMAGE, 0);

            if (damagingTime > 0) return;

            setAnimation(AnimationKey.DAMAGE);

            setVelocity((getDirection()) * 5, -5);
        }

        setHealthBy(getHealth() + toHealth, damaging);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
        setScaleY(alive ? 1.0f : -1.0f);
        setCollisionEnabled(alive);
        if (!alive) setVelocityY(-4);
        healthBar.setVisible(alive);

        if(alive) setVisible(true);
    }

    private void death(IDamaging damaging) {
        getMapkitItem().playSound(SoundKey.DEATH);
        setAlive(false);
        health = 0;

        if(damaging == null) {
            if(isOnWorld()) {
                world.add(Particle.create(500, Color.of(0x220000)), getX(), getY());
                setVisible(false);
            }
        }

        if (D2D2World.isServer()) {
            world.dispatchEvent(new WorldEvent(WorldEvent.ACTOR_DEATH, world,
                            null,
                            getGameObjectId(),
                            damaging != null ? damaging.getGameObjectId() : 0
                    )
            );
        }
    }

    @Override
    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollision(float x, float y, float w, float h) {
        this.collisionX = x;
        this.collisionY = y;
        this.collisionWidth = w;
        this.collisionHeight = h;
    }

    @Override
    public void setCollisionX(float collisionX) {
        this.collisionX = collisionX;
    }

    @Override
    public void setCollisionY(float collisionY) {
        this.collisionY = collisionY;
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
        this.collisionWidth = collisionWidth;
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public void reset() {
        setXY(getStartX(), getStartY());
        setHealthBy(getMaxHealth(), null);
        getController().reset();
        setAlive(true);
        repair();
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public void setFloorOnly(boolean b) {
    }

    @Override
    public boolean isFloorOnly() {
        return false;
    }

    @Override
    public void setFloor(ICollision floor) {
        this.floor = floor;
    }

    @Override
    public ICollision getFloor() {
        return floor;
    }

    @Override
    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    @Override
    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    @Override
    public void setVelocity(float vX, float vY) {
        setVelocityX(vX);
        setVelocityY(vY);
    }

    @Override
    public float getVelocityX() {
        return velocityX;
    }

    @Override
    public float getVelocityY() {
        return velocityY;
    }

    public void attack() {
        if (!D2D2World.isServer() || !isAlive()) return;

        attackTime = ATTACK_TIME;
        if (getWeapon() != null)
            getWorld().actorAttack(this, getWeapon());
    }

    public void jump() {
        if (floor != null) {
            onJump = true;
            getMapkitItem().playSound(SoundKey.JUMP, 0);
            setVelocityY(getVelocityY() + -getJumpPower());
            setFloor(null);
        }
    }

    public void go(final int direction) {
        setDirection(direction);

        if (!isGravityEnabled()) return;

        if (getDirection() == Direction.RIGHT) {
            setVelocityX(
                    getVelocityX() + getSpeed()
            );
        } else if (getDirection() == Direction.LEFT) {
            setVelocityX(
                    getVelocityX() - getSpeed()
            );
        }
    }

    @Property
    public float getJumpPower() {
        return jumpPower;
    }

    @Property
    public void setJumpPower(float jumpPower) {
        this.jumpPower = jumpPower;
    }

    @Override
    public void process() {
        if (!D2D2World.isServer()) return;

        setAnimation(AnimationKey.IDLE);

        if (attackTime > 0) {
            if (getVelocityY() == 0)
                setAnimation(AnimationKey.ATTACK);
        }

        Controller c = getController();

        boolean left = c.isLeft();
        boolean right = c.isRight();

        if (isAlive()) {

            if (left || right) {
                int direction = left ? -1 : 1;
                go(direction);

                if (attackTime == 0 && !c.isB()) {
                    setAnimation(AnimationKey.WALK);
                } else {
                    setAnimation(AnimationKey.WALK_ATTACK);
                }
            } else if (attackTime == 0)
                setAnimation(AnimationKey.IDLE);

            if (c.isB()) {
                if (attackTime == 0)
                    attack();
            }

            if (c.isA() && getFloor() != null && !onJump) {
                jump();
                jumpTime = JUMP_TIME;
            }

            if (!c.isA() && getFloor() != null) onJump = false;
        }

        if (attackTime > 1) attackTime--;
        if (attackTime == 1 && !c.isB()) attackTime--;

        if (jumpTime > 0) {
            jumpTime--;
            if (c.isA()) setVelocityY(getVelocityY() - 1f);
        }

        if (getFloor() == null) {

            setAnimation(
                    getVelocityY() < 0 ?
                            (attackTime == 0 ? AnimationKey.JUMP : AnimationKey.JUMP_ATTACK) :
                            (attackTime == 0 ? AnimationKey.FALL : AnimationKey.FALL_ATTACK)

            );
        } else if (getFloor() instanceof final IMovable movableFloor) {
            final float toX = movableFloor.getMovingSpeedX();
            final float toY = movableFloor.getMovingSpeedY();

            if (toY != 0) {
                setVelocityY(toY);
                //moveY(toY);
            }
            if (toX != 0) {
                setVelocityX(getVelocityX() + toX / 3);
                //moveX(toX);
            }
        }

        movingSpeedX = movingSpeedY = 0.0f;
    }

    public boolean isOnJump() {
        return onJump;
    }

    @Override
    public float getWidth() {
        return collisionWidth;
    }

    @Override
    public float getHeight() {
        return collisionHeight;
    }

    protected boolean isAttackingNow() {
        return attackTime > 0;
    }

    @Override
    public Controller getController() {
        return controller;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void setGravityEnabled(boolean b) {
        this.fallEnabled = b;

        if (!fallEnabled)
            setVelocity(0f, 0f);

    }

    @Override
    public boolean isGravityEnabled() {
        return fallEnabled;
    }

    public float getWeaponY() {
        return weaponLocationY;
    }

    public void setWeaponY(float weaponLocationY) {
        this.weaponLocationY = weaponLocationY;
    }

    public float getWeaponX() {
        return weaponLocationX;
    }

    public void setWeaponX(float weaponLocationX) {
        this.weaponLocationX = weaponLocationX;
    }

    public void setWeaponLocation(final float x, final float y) {
        setWeaponX(x);
        setWeaponY(y);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        weapon.setOwner(this);
    }

    @Override
    public void setAnimation(int animationKey, boolean loop) {
        if (damagingTime > 0) return;
        super.setAnimation(animationKey, loop);
    }

    @Override
    public void onCollide(ICollision collideWith) {

    }

    @Override
    public void setPushable(boolean b) {
        this.pushable = b;
    }

    @Override
    public boolean isPushable() {
        return this.pushable;
    }

    @Override
    public void move(float toX, float toY) {
        movingSpeedX = toX;
        movingSpeedY = toY;
        super.moveX(toX);
        super.moveY(toY);
    }

    @Override
    public void moveX(float value) {
        movingSpeedX = value;
        super.moveX(value);
    }

    @Override
    public void moveY(float value) {
        movingSpeedY = value;
        super.moveY(value);
    }

    @Override
    public float getMovingSpeedX() {
        return movingSpeedX;
    }

    @Override
    public float getMovingSpeedY() {
        return movingSpeedY;
    }

    @Override
    public void setMovingSpeedX(float movingSpeedX) {
        this.movingSpeedX = movingSpeedX;
    }
}






























