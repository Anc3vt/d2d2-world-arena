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

import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.control.Controller;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.scene.Particle;
import com.ancevt.d2d2world.ui.HealthBar;
import com.ancevt.d2d2world.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2world.constant.AnimationKey.*;

abstract public class Actor extends Animated implements
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

    private static final int JUMP_TIME = 4;
    private static final int DAMAGING_TIME = 14;
    private final FramedSprite framedDoHead;
    private final List<Weapon> weapons;
    private float weaponLocationX, weaponLocationY;
    private Weapon weapon;
    private int weaponIndex;

    private int attackTime;
    private int jumpTime;
    private int damagingTime;
    private boolean onJump;

    private int health, maxHealth;
    private float weight;
    private ICollision floor;
    private float velocityX, velocityY;
    private float jumpPower;
    private Controller controller;

    protected BitmapText bitmapTextDebug;
    private boolean fallEnabled;

    private boolean alive;
    private boolean pushable;
    private final HealthBar healthBar;
    private final DisplayObjectContainer weaponContainer;
    private final DisplayObjectContainer headContainer;
    private final Sprite armSprite;
    private float weaponDegree;
    private int goDirection;
    private float aimX;
    private float aimY;

    public Actor(MapkitItem mapkitItem, final int gameObjectId) {
        super(mapkitItem, gameObjectId);
        healthBar = new HealthBar();
        weapons = new ArrayList<>();

        weaponContainer = new DisplayObjectContainer();


        framedDoHead = new FramedSprite(mapkitItem
                .getTextureAtlas()
                .createTextures(mapkitItem.getDataEntry().getString(DataKey.HEAD))
        );
        framedDoHead.setSlowing(SLOWING);
        framedDoHead.setFrame(0);
        framedDoHead.setLoop(true);
        framedDoHead.play();
        headContainer = new DisplayObjectContainer();

        armSprite = new Sprite(mapkitItem.getTextureAtlas()
                .createTexture(mapkitItem.getDataEntry()
                        .getString(DataKey.ARM)));

        weaponContainer.add(armSprite, 2, -9);

        headContainer.add(framedDoHead, -framedDoHead.getWidth() / 2, -16);

        add(headContainer, 0);
        add(weaponContainer, 0);
        headContainer.setXY(0, -4);


        add(healthBar, -healthBar.getWidth() / 2, -24);
        setPushable(true);
        setGravityEnabled(true);
        setAlive(true);
        setCollisionEnabled(true);
        setAnimation(IDLE);
        setDirection(Direction.RIGHT);
        setController(new Controller());
        getController().setControllerChangeListener(c -> setAnimation(IDLE));
    }

    @Override
    protected void fixXY() {
        if (weapon != null) {

            switch (getDirection()) {
                case Direction.LEFT -> {
                    IDisplayObject d = weapon.getDisplayObject();
                    d.setScaleX(-1);
                    d.setX(d.getWidth() - getWeaponX() - d.getWidth() / 2);
                    armSprite.setScaleX(-1);
                    armSprite.setXY(-2, -8);
                }
                case Direction.RIGHT -> {
                    IDisplayObject d = weapon.getDisplayObject();
                    d.setScaleX(1);
                    d.setX(getWeaponX() - d.getWidth() / 2);
                    armSprite.setScaleX(1);
                    armSprite.setXY(2, -8);
                }
            }


        }
        super.fixXY();
    }

    private void fixBodyPartsY() {
        if (weapon != null) {

            IDisplayObject weaponDisplayObject = weapon.getDisplayObject();
            float w = weaponDisplayObject.getWidth();
            float h = weaponDisplayObject.getHeight();
            if (getAnimation() == WALK_ATTACK) {
                //armSprite.setY(-12);
                //weapon.getDisplayObject().setY(getWeaponY() - h / 2 - (getAnimation() == WALK_ATTACK ? 4 : 0));
            } else {
                armSprite.setY(-8);
                weapon.getDisplayObject().setY(getWeaponY() - h / 2 - (getAnimation() == WALK_ATTACK ? 4 : 0));
            }


        }

        if (headContainer != null) {
            switch (getAnimation()) {
                case WALK, WALK_ATTACK -> {
                    headContainer.setXY(3 * getDirection(), -5);
                }
                default -> {
                    headContainer.setXY(0, -4);
                }
            }
        }

        if (weaponContainer != null) {
            switch (getAnimation()) {
                case WALK, JUMP, FALL, DAMAGE -> weaponContainer.setVisible(false);
                default -> weaponContainer.setVisible(true);
            }
        }
    }

    public void attack() {
        fixBodyPartsY();

        attackTime = getWeapon().getAttackTime();
        getWeapon().playShootSound();

        if (!D2D2World.isServer() || !isAlive() || damagingTime > 0) return;

        if (getWeapon() != null) {
            getWorld().actorAttack(getWeapon());
            getWorld().getSyncDataAggregator().attack(this);
        }
    }

    @Override
    public void setAnimation(int animationKey, boolean loop) {
        if (damagingTime > 0) return;
        if (animationKey == getAnimation()) return;

        if (framedDoHead != null) {
            framedDoHead.setFrame(0);
        }

        super.setAnimation(animationKey, loop);

        fixBodyPartsY();
    }

    @Override
    public void setDirection(int direction) {
        super.setDirection(direction);
        weaponContainer.setScale(direction, direction);
        headContainer.setScaleY(direction);
        fixBodyPartsY();
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

        float deg = RotationUtils.getDegreeBetweenPoints(getX(), getY(), aimX, aimY);

        if (aimX < getX()) {
            setDirection(-1);
        } else {
            setDirection(1);
        }
        setBackward(goDirection != getDirection());

        weaponContainer.setRotation(-deg);
        weaponDegree = -deg;

        if (getDirection() == Direction.RIGHT) {
            if (deg > 22) deg = 22;
            else if (deg < -30) deg = -30;

            headContainer.setRotation(-deg);
        } else {
            deg += 100;

            if (deg > 0 && deg < 255) deg = 255;
            else if (deg < 0 && deg >= -60) deg = -60;

            headContainer.setRotation(-deg + 100);
        }
    }

    public void setAimXY(float targetX, float targetY) {
        this.aimX = targetX;
        this.aimY = targetY;

        if (isOnWorld()) getWorld().getSyncDataAggregator().aim(this);
    }

    public float getAimX() {
        return aimX;
    }

    public float getAimY() {
        return aimY;
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
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;

        this.health = health;
        healthBar.setValue(health);

        if (health <= 0 && isAlive()) death(null);

        if (isOnWorld()) getWorld().getSyncDataAggregator().health(this, null);
    }

    @Override
    public void setHealthBy(int health, IDamaging damaging, boolean fromServer) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;

        if (health < oldHealth) damageBlink();

        if (fromServer || D2D2World.isServer()) {
            this.health = health;
            healthBar.setValue(health);
            if (health <= 0 && isAlive()) death(damaging);
        }
        if (isOnWorld()) {
            getWorld().getSyncDataAggregator().health(this, damaging);
        }
    }

    @Override
    public void damage(int toHealth, IDamaging damaging) {
        if (toHealth > 0) {
            if (getHealth() > toHealth)
                getMapkitItem().playSound(SoundKey.DAMAGE, 0);

            if (damagingTime > 0) return;
            setAnimation(AnimationKey.DAMAGE);
            setVelocity(getDirection() * -2, -2);
        }

        setHealthBy(getHealth() - toHealth, damaging, false);
    }

    private void damageBlink() {
        damagingTime = DAMAGING_TIME;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void repair() {
        setAnimation(AnimationKey.IDLE);
        setHealth(getMaxHealth());
        setAlive(true);

        if (isOnWorld()) getWorld().getSyncDataAggregator().repair(this);
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

        if (alive) setVisible(true);
    }

    private void death(IDamaging damaging) {
        getMapkitItem().playSound(SoundKey.DEATH);
        setAlive(false);
        health = 0;

        if (damaging == null) {
            if (isOnWorld()) {
                getWorld().add(Particle.bloodExplosion(500, Color.of(0x220000)), getX(), getY());
                setVisible(false);
            }
        }

        if (D2D2World.isServer()) {

            getWorld().dispatchEvent(WorldEvent.builder()
                    .type(WorldEvent.ACTOR_DEATH)
                    .deadActorGameObjectId(getGameObjectId())
                    .killerGameObjectId(damaging != null ? damaging.getGameObjectId() : 0)
                    .build());
        }
    }

    @Override
    public void reset() {
        setXY(getStartX(), getStartY());
        setHealth(getMaxHealth());
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
        if (floor != null) setAnimation(IDLE);
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

    public void jump() {
        if (floor != null) {
            onJump = true;
            getMapkitItem().playSound(SoundKey.JUMP, 0);
            setVelocityY(getVelocityY() + -getJumpPower());
            setFloor(null);
        }
    }

    public void go(final int direction) {
        //setDirection(direction);

        goDirection = direction;

        if (!isGravityEnabled()) return;

        if (direction == Direction.RIGHT) {
            setVelocityX(getVelocityX() + getSpeed());
        } else if (direction == Direction.LEFT) {
            setVelocityX(getVelocityX() - getSpeed());
        }
    }

    public float getArmDegree() {
        return weaponDegree;
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
        //if (!D2D2World.isServer()) return;

        //setAnimation(AnimationKey.IDLE);

        if (attackTime > 0) {
            if (getVelocityY() == 0)
                setAnimation(ATTACK);
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
            }
            //else if (attackTime == 0) setAnimation(AnimationKey.IDLE);

            if (c.isB()) {
                if (attackTime == 0) attack();
            }

            if (c.isA() && getFloor() != null && !onJump) {
                jump();
                jumpTime = JUMP_TIME;
            }

            if (!c.isA() && getFloor() != null) onJump = false;
        }

        if (attackTime >= 1) attackTime--;
        //if (attackTime == 1 && !c.isB()) attackTime--;

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

        setMovingSpeedX(0f);
        setMovingSpeedY(0f);
    }

    public boolean isOnJump() {
        return onJump;
    }

    @Override
    public float getWidth() {
        return getCollisionWidth();
    }

    @Override
    public float getHeight() {
        return getCollisionHeight();
    }

    protected boolean isAttackingNow() {
        return attackTime > 0;
    }

    @Override
    public Controller getController() {
        return controller;
    }

    @Override
    public void setController(@NotNull Controller controller) {
        this.controller = controller;
        controller.setControllerChangeListener(c -> {
            if (c.getState() == 0) setAnimation(IDLE);
        });
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

    @Property
    public float getWeaponY() {
        return weaponLocationY;
    }

    @Property
    public void setWeaponY(float weaponLocationY) {
        this.weaponLocationY = weaponLocationY;
    }

    @Property
    public float getWeaponX() {
        return weaponLocationX;
    }

    @Property
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

    public void setWeapon(@NotNull String weaponClassName) {
        weapons.stream()
                .filter(w -> w.getClass().getName().equals(weaponClassName))
                .findAny()
                .ifPresentOrElse(this::setWeapon, ()->{
                    Weapon weapon = Weapon.createWeapon(weaponClassName);
                    addWeapon(weapon, weapon.getMaxAmmunition());
                    setWeapon(weapon);
                });
    }

    public void setWeapon(@NotNull Weapon weapon) {
        if (this.weapon != null) {
            this.weapon.getDisplayObject().removeFromParent();
        }

        this.weapon = weapon;
        weapon.setOwner(this);
        weaponContainer.add(weapon.getDisplayObject());
        fixXY();

        if (isOnScreen()) getWorld().getSyncDataAggregator().weapon(this);
    }

    public void nextWeapon() {
        weaponIndex++;
        if (weaponIndex >= weapons.size()) {
            weaponIndex = 0;
        }
        setWeapon(weapons.get(weaponIndex));
    }

    public void prevWeapon() {
        weaponIndex--;
        if (weaponIndex < 0) {
            weaponIndex = weapons.size() - 1;
        }
        setWeapon(weapons.get(weaponIndex));
    }

    public void addWeapon(Weapon weapon, int ammunition) {
        weapons.stream()
                .filter(w -> w.getClass() == weapon.getClass())
                .findAny()
                .ifPresentOrElse(w -> {
                    w.setAmmunition(w.getAmmunition() + ammunition);
                }, () -> {
                    Weapon newWeapon = Weapon.createWeapon(weapon.getClass().getName());
                    newWeapon.setAmmunition(ammunition);
                    weapons.add(newWeapon);
                });
    }

    public List<Weapon> getWeapons() {
        return List.copyOf(weapons);
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
        setMovingSpeedX(toX);
        setMovingSpeedY(toY);
        super.moveX(toX);
        super.moveY(toY);
    }

    @Override
    public void moveX(float value) {
        setMovingSpeedX(value);
        super.moveX(value);
    }

    @Override
    public void moveY(float value) {
        setMovingSpeedY(value);
        super.moveY(value);
    }

    public void mouseMove(float worldX, float worldY) {
        aimX = worldX;
        aimY = worldY;
    }
}






























