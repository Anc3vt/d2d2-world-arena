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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.commons.Holder;
import com.ancevt.commons.Pair;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.control.Controller;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.gameobject.area.AreaWater;
import com.ancevt.d2d2world.gameobject.weapon.FireWeapon;
import com.ancevt.d2d2world.gameobject.weapon.StandardWeapon;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.ui.HealthBar;
import com.ancevt.d2d2world.world.WorldEvent;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2world.D2D2World.isClient;
import static com.ancevt.d2d2world.D2D2World.isServer;
import static com.ancevt.d2d2world.constant.AnimationKey.ATTACK;
import static com.ancevt.d2d2world.constant.AnimationKey.DAMAGE;
import static com.ancevt.d2d2world.constant.AnimationKey.FALL;
import static com.ancevt.d2d2world.constant.AnimationKey.FALL_ATTACK;
import static com.ancevt.d2d2world.constant.AnimationKey.HOOK;
import static com.ancevt.d2d2world.constant.AnimationKey.IDLE;
import static com.ancevt.d2d2world.constant.AnimationKey.JUMP;
import static com.ancevt.d2d2world.constant.AnimationKey.JUMP_ATTACK;
import static com.ancevt.d2d2world.constant.AnimationKey.SLOWING;
import static com.ancevt.d2d2world.constant.AnimationKey.WALK;
import static com.ancevt.d2d2world.constant.AnimationKey.WALK_ATTACK;

abstract public class Actor extends Animated implements
        IProcessable,
        IDirectioned,
        IMovable,
        IAnimated,
        IDestroyable,
        ITight,
        IResettable,
        IGravitational,
        IControllable,
        ISpeedable,
        IHookable,
        ISonicSynchronized {

    private static final int JUMP_TIME = 4;
    private static final int DAMAGING_TIME = 14;
    private static final int WEAPON_SWITCH_TIME = 4;
    private static final int HOOK_TIME = 10;
    private static final int UNDER_WATER_TIME = 2000;

    private final FramedSprite headFramedDisplayObject;
    private final List<Weapon> weapons;
    private float weaponLocationX, weaponLocationY;
    private Weapon currentWeapon;
    private int weaponIndex;

    private int attackTime;
    private int jumpTime;
    private int damagingTime;
    private int hookTime;
    private int weaponSwitchTime;
    private int underWaterTime;
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
    private final Container weaponContainer;
    private final Container headContainer;
    private final Sprite armSprite;
    private float weaponDegree;
    private int goDirection;
    private float aimX;
    private float aimY;
    private boolean collisionEnabled;
    private float collisionWidth;
    private float collisionHeight;
    private float collisionX;
    private float collisionY;
    private float speed;
    private float movingSpeedY;
    private float movingSpeedX;
    private float startY;
    private float startX;
    private AreaHook hook;
    private int tact;
    private AreaWater areaWater;
    private IDisplayObject weaponDisplayObject;

    public Actor(MapkitItem mapkitItem, final int gameObjectId) {
        super(mapkitItem, gameObjectId);
        healthBar = new HealthBar();
        weapons = new ArrayList<>();

        weaponContainer = new Container();
        resetWeapons();

        headFramedDisplayObject = new FramedSprite(mapkitItem
                .getTextureAtlas()
                .createTextures(mapkitItem.getDataEntry().getString(DataKey.HEAD))
        );
        headFramedDisplayObject.setSlowing(SLOWING);
        headFramedDisplayObject.setFrame(0);
        headFramedDisplayObject.setLoop(true);
        headFramedDisplayObject.play();
        headContainer = new Container();

        armSprite = new Sprite(mapkitItem.getTextureAtlas()
                .createTexture(mapkitItem.getDataEntry()
                        .getString(DataKey.ARM)));

        weaponContainer.add(armSprite, 2, -9);

        headContainer.add(headFramedDisplayObject, -headFramedDisplayObject.getWidth() / 2, -16);

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
    public void process() {
        tact++;

        if (attackTime > 0) {
            if (getVelocityY() == 0) setAnimation(ATTACK);
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

            if (c.isB()) {
                if (attackTime == 0) attack();
            }

            if (c.isA() && getFloor() != null && !onJump) {
                jump();
                jumpTime = JUMP_TIME;
            }

            if (c.isA() && getHook() != null && !onJump) {
                if (c.isDown()) {
                    setHook(null);
                    setGravityEnabled(true);
                } else {
                    jump();
                }
                jumpTime = JUMP_TIME;
            } else if (c.isDown() && getHook() != null && !onJump) {
                setHook(null);
                setGravityEnabled(true);
            }

            if (!c.isA() && (getFloor() != null || getHook() != null)) onJump = false;
        }

        if (attackTime >= 1) attackTime--;

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

        if (weaponSwitchTime > 0) weaponSwitchTime--;

        if (hookTime > 0 && getHook() == null) hookTime--;

        if (underWaterTime >= UNDER_WATER_TIME) {
            damage(5, areaWater);
            underWaterTime = UNDER_WATER_TIME - 200;
        }

        if (underWaterTime > 0) {
            underWaterTime--;
        }

        if (isServer() && getHealth() < 26 && tact % 50 == 0) {
            setHealth(getHealth() + 1);
        }

    }

    public boolean underWater(AreaWater areaWater) {
        this.areaWater = areaWater;
        underWaterTime += 2;
        return underWaterTime >= UNDER_WATER_TIME;
    }

    public void resetUnderWater() {
        underWaterTime = 0;
    }

    public void resetWeapons() {
        weapons.clear();
        addWeapon(StandardWeapon.class, 100);
        setCurrentWeaponClass(StandardWeapon.class);
    }

    @Override
    protected void fixXY() {
        if (currentWeapon != null) {

            Pair<Float, Float> weaponXYOffset = Weapon.getXYOffset(currentWeapon.getClass());

            switch (getDirection()) {
                case Direction.LEFT -> {
                    weaponDisplayObject.setScaleX(-1);
                    weaponDisplayObject.setX(weaponDisplayObject.getWidth() - getWeaponX() - 32 + weaponXYOffset.getFirst());
                    weaponDisplayObject.setY(weaponXYOffset.getSecond());
                    armSprite.setScaleX(-1);
                    armSprite.setXY(-2, -8);
                }
                case Direction.RIGHT -> {
                    weaponDisplayObject.setScaleX(1);
                    weaponDisplayObject.setX(getWeaponX() - weaponXYOffset.getFirst());
                    weaponDisplayObject.setY(weaponXYOffset.getSecond());
                    armSprite.setScaleX(1);
                    armSprite.setXY(2, -8);
                }
            }


        }
        super.fixXY();
    }

    private void fixHeadContainerXY() {
        if (headContainer != null) {
            switch (getAnimation()) {
                case WALK, WALK_ATTACK -> {
                    headContainer.setXY(3 * getDirection(), -5);
                }
                case JUMP, JUMP_ATTACK -> {
                    headContainer.setXY(0, -6);
                }
                case FALL, FALL_ATTACK -> {
                    headContainer.setXY(3 * getDirection(), -6);
                }
                default -> {
                    headContainer.setXY(0, -4);
                }
            }
        }

        if (weaponContainer != null) {
            weaponContainer.setVisible(getAnimation() != DAMAGE);
        }
    }

    public void attack() {
        attackTime = getCurrentWeapon().getAttackTime();

        if (getCurrentWeapon().getAmmunition() <= 0) {
            weapons.remove(getCurrentWeapon());
            nextWeapon();
            return;
        }

        if (getCurrentWeaponClassname().equals(FireWeapon.class.getName())) {
            if (underWaterTime != 0) return;
        }

        if (isAlive()) {
            // client-side shooting
            if (isServer()) {
                if (getCurrentWeapon() != null) {
                    getCurrentWeapon().shoot(getWorld());
                    getWorld().getSyncDataAggregator().changeWeaponState(
                            this,
                            getCurrentWeapon().getClass().getName(),
                            getCurrentWeapon().getAmmunition()
                    );
                }

                if (isOnWorld()) {
                    getWorld().dispatchEvent(WorldEvent.builder()
                            .type(WorldEvent.ACTOR_ATTACK)
                            .actor(this)
                            .build()
                    );
                }
            } else { // server-side shooting
                if (getCurrentWeapon() != null) {
                    getCurrentWeapon().shoot(getWorld());
                }
            }
        }
    }

    @Override
    public void setAnimation(int animationKey, boolean loop) {
        if (damagingTime > 0 || animationKey == getAnimation()) return;

        if (headFramedDisplayObject != null) {
            headFramedDisplayObject.setFrame(0);
            if (animationKey == DAMAGE) {
                headFramedDisplayObject.removeFromParent();
            } else if (!headFramedDisplayObject.hasParent()) {
                headContainer.add(headFramedDisplayObject);
            }
        }

        super.setAnimation(animationKey, loop);
        fixHeadContainerXY();
    }

    @Override
    public void setDirection(int direction) {
        if (getDirection() == direction) return;

        super.setDirection(direction);
        weaponContainer.setScale(direction, direction);
        headContainer.setScaleY(direction);
        fixHeadContainerXY();
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
            bitmapTextDebug.setSize(1000, 1000);
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

        float deg = RadialUtils.getDegreeBetweenPoints(getX(), getY(), aimX, aimY);

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
        setHealthBy(health, null, false);
    }

    @Override
    public void setHealthBy(int health, IDamaging damaging, boolean fromServer) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;

        if (health < oldHealth) {
            damageBlink();
            playSound("character-damage.ogg");
        }

        this.health = health;
        if (health <= 0 && isAlive()) death(damaging);
        healthBar.setValue(health);

        if (isClient() && !fromServer) {
            if(isOnWorld()) getWorld().getSyncClientDataSender().health(this, damaging);
        }

        if (isOnWorld()) getWorld().getSyncDataAggregator().health(this, damaging);
    }

    @Override
    public void damage(int fromHealth, IDamaging damaging) {
        if (fromHealth > 0) {
            if (damagingTime > 0) return;
            if (attackTime == 0) setAnimation(AnimationKey.DAMAGE);
        }

        setHealthBy(getHealth() - fromHealth, damaging, false);
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

        dispatchEvent(ActorEvent.builder().type(ActorEvent.ACTOR_REPAIR).build());
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
        setAlive(false);
        setGravityEnabled(true);
        setHook(null);
        health = 0;

        if (damaging == null) {
            if (isOnWorld()) {
                getWorld().add(Particle.bloodExplosion(500, Color.of(0x220000)), getX(), getY());
                setVisible(false);
            }
        }

        dispatchEvent(ActorEvent.builder().type(ActorEvent.ACTOR_DEATH).build());

        if (isServer()) {
            getWorld().dispatchEvent(WorldEvent.builder()
                    .type(WorldEvent.ACTOR_DEATH)
                    .deadActorGameObjectId(getGameObjectId())
                    .actor(this)
                    .killerGameObjectId(
                            damaging != null && damaging.getDamagingOwnerActor() != null ?
                                    damaging.getDamagingOwnerActor().getGameObjectId() : 0)
                    .build());
        }

        resetWeapons();
    }

    @Override
    public void reset() {
        setXY(getStartX(), getStartY());
        setHealth(getMaxHealth());
        getController().reset();
        setAlive(true);
        repair();

        if (isOnWorld()) getWorld().getSyncDataAggregator().reset(this);
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

    public void go(final int direction) {
        goDirection = direction;

        if (isGravityEnabled()) {
            if (floor != null) {
                setVelocityX(direction == Direction.RIGHT ? velocityX + speed : velocityX - speed );
            } else {
                setVelocityX(direction == Direction.RIGHT ? velocityX + speed / 5f : velocityX - speed / 5f);
            }
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
    public void setHook(AreaHook hook) {
        if (this.hook == hook) return;

        if (hook == null) {
            this.hook = null;
            if (isOnWorld()) getWorld().getSyncDataAggregator().hook(this, null);
            dispatchEvent(ActorEvent.builder()
                    .type(ActorEvent.ACTOR_HOOK)
                    .hookGameObjectId(0)
                    .build());
            return;
        }

        if (hookTime == 0) {
            float x = hook.getX() + hook.getWidth() / 2;
            float y = hook.getY() + hook.getHeight() / 2;

            setXY(x, y + 24);
            setAnimation(HOOK, false);
            setGravityEnabled(false);
            this.hook = hook;
            hookTime = HOOK_TIME;

            if (isOnWorld()) getWorld().getSyncDataAggregator().hook(this, hook);
            dispatchEvent(ActorEvent.builder()
                    .type(ActorEvent.ACTOR_HOOK)
                    .hookGameObjectId(hook.getGameObjectId())
                    .build());
        }
    }

    @Override
    public AreaHook getHook() {
        return hook;
    }

    public void jump() {
        if (floor != null || getHook() != null) {
            setHook(null);

            setGravityEnabled(true);

            onJump = true;
            setVelocityY(-getJumpPower());
            setFloor(null);
        }
    }

    @Override
    public int tact() {
        return tact;
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
        if (!fallEnabled) setVelocity(0f, 0f);
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

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeaponClass(@NotNull Class<? extends Weapon> cls) {
        setCurrentWeaponClassname(cls.getName());
    }

    @Property
    public String getCurrentWeaponClassname() {
        return currentWeapon.getClass().getName();
    }

    @SneakyThrows
    @Property
    public void setCurrentWeaponClassname(@NotNull String weaponClassname) {
        //if (weaponSwitchTime > 0) return;
        weaponSwitchTime = WEAPON_SWITCH_TIME;

        if (weaponDisplayObject != null) {
            weaponDisplayObject.removeFromParent();
        }

        var weaponClass = (Class<? extends Weapon>) Class.forName(weaponClassname);
        Method method = weaponClass.getMethod("createSprite");
        weaponDisplayObject = (Sprite) method.invoke(null);
        weaponContainer.add(weaponDisplayObject);

        fixXY();

        weapons.stream()
                .filter(w -> w.getClass().getName().equals(weaponClassname))
                .findAny()
                .ifPresent(w -> {
                    playSound("weapon-switch.ogg");

                    currentWeapon = w;
                    currentWeapon.setOwner(this);

                    dispatchEvent(ActorEvent.builder()
                            .type(ActorEvent.SET_WEAPON)
                            .weaponClassName(weaponClassname)
                            .build()
                    );

                    if (isOnScreen()) getWorld().getSyncDataAggregator().switchWeapon(this);
                });
    }

    public void setWeaponAmmunition(@NotNull String weaponClassname, int ammunition) {
        weapons.stream()
                .filter(w -> w.getClass().getName().equals(weaponClassname))
                .findAny()
                .ifPresent(w -> {
                    w.setAmmunition(ammunition);
                    dispatchEvent(ActorEvent.builder()
                            .type(ActorEvent.AMMUNITION_CHANGE)
                            .weaponClassName(weaponClassname)
                            .build()
                    );
                });
    }

    public void nextWeapon() {
        final int oldWeaponIndex = weaponIndex;
        weaponIndex++;
        if (weaponIndex >= weapons.size()) {
            weaponIndex = 0;
        }
        if (weapons.size() == 0) {
            addWeapon(StandardWeapon.class.getName(), 5);
            return;
        }

        if (weaponIndex == oldWeaponIndex) return;

        setCurrentWeaponClassname(weapons.get(weaponIndex).getClass().getName());
    }

    public void prevWeapon() {
        final int oldWeaponIndex = weaponIndex;
        weaponIndex--;
        if (weaponIndex < 0) {
            weaponIndex = weapons.size() - 1;
        }
        if (weapons.size() == 0) {
            addWeapon(StandardWeapon.class.getName(), 5);
            return;
        }

        if (weaponIndex == oldWeaponIndex) return;

        if (weaponIndex >= weapons.size()) weaponIndex = 0;

        setCurrentWeaponClassname(weapons.get(weaponIndex).getClass().getName());
    }

    public boolean addWeapon(@NotNull Class<? extends Weapon> cls, int ammunition) {
        return addWeapon(cls.getName(), ammunition);
    }

    public boolean addWeapon(@NotNull String weaponClassname, int ammunition) {
        Holder<Boolean> resultHolder = new Holder<>(false);
        Holder<Weapon> weaponHolder = new Holder<>();

        weapons.stream()
                .filter(w -> w.getClass().getName().equals(weaponClassname))
                .findAny()
                .ifPresentOrElse(weapon -> {
                    boolean result = weapon.addAmmunition(ammunition);
                    resultHolder.setValue(result);
                    weaponHolder.setValue(weapon);
                }, () -> {
                    Weapon newWeapon = Weapon.createWeapon(weaponClassname);
                    boolean result = newWeapon.setAmmunition(ammunition);
                    weapons.add(newWeapon);
                    resultHolder.setValue(result);
                    weaponHolder.setValue(newWeapon);

                    dispatchEvent(ActorEvent.builder()
                            .type(ActorEvent.AMMUNITION_CHANGE)
                            .weaponClassName(weaponClassname)
                            .ammunition(ammunition)
                            .build());
                });

        if (resultHolder.getValue()) {
            dispatchEvent(ActorEvent.builder()
                    .type(ActorEvent.AMMUNITION_CHANGE)
                    .weaponClassName(weaponClassname)
                    .ammunition(ammunition)
                    .build());

            if (isOnWorld()) {
                getWorld().getSyncDataAggregator().addWeapon(this, weaponClassname);
                getWorld().getSyncDataAggregator().changeWeaponState(this, weaponClassname, weaponHolder.getValue().getAmmunition());
            }
        }

        return resultHolder.getValue();
    }

    public List<Weapon> getWeapons() {
        return List.copyOf(weapons);
    }

    @Override
    public void setCollisionEnabled(boolean value) {
        this.collisionEnabled = value;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
        this.collisionWidth = collisionWidth;
    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public void setCollisionX(float collisionX) {
        this.collisionX = collisionX;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public void setCollisionY(float collisionY) {
        this.collisionY = collisionY;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
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

    public void setHealthBarVisible(boolean b) {
        healthBar.setVisible(b);
    }

    public boolean isHealthBarVisible() {
        return healthBar.isVisible();
    }

    @Override
    public void setStartX(float x) {
        this.startX = x;
    }

    @Override
    public void setStartY(float y) {
        this.startY = y;
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
    public float getMovingSpeedX() {
        return movingSpeedX;
    }

    @Override
    public float getMovingSpeedY() {
        return movingSpeedY;
    }

    @Override
    public void setMovingSpeedX(float value) {
        this.movingSpeedX = value;
    }

    @Override
    public void setMovingSpeedY(float value) {
        this.movingSpeedY = value;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons.clear();
        this.weapons.addAll(weapons);
    }

    public void setWeaponVisible(boolean b) {
        weaponDisplayObject.setVisible(false);
    }

    public boolean isWeaponVisible() {
        return weaponDisplayObject.isVisible();
    }
}






























