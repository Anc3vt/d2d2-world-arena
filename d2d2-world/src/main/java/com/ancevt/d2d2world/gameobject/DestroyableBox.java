package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.fx.Particle;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.D2D2World.isServer;
import static com.ancevt.d2d2world.data.DataKey.DAMAGE_SOUND;
import static com.ancevt.d2d2world.data.DataKey.DESTROY_SOUND;

public class DestroyableBox extends DisplayObjectContainer implements
        IDestroyable,
        ISynchronized,
        ISonicSynchronized,
        ITight,
        IGravitational {

    private static final int DAMAGING_TIME = 5;

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    private boolean collisionEnabled;
    private float collisionWidth;
    private float collisionHeight;
    private float collisionX;
    private float collisionY;
    private int health;
    private int maxHealth;
    private int damagingTime;
    private World world;

    private final Sprite sprite;
    private Sprite blinkSprite;
    private Sprite[] brokenParts;
    private boolean pushable;
    private boolean floorOnly;
    private float movingSpeedY;
    private float movingSpeedX;
    private float startY;
    private float startX;
    private boolean gravityEnabled;
    private float velocityY;
    private float velocityX;
    private ICollision floor;
    private float weight;
    private boolean destroyed;
    private String pickupClassname;
    private String weaponClassname;
    private int pickupValue;

    public DestroyableBox(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        if (mapkitItem.getDataEntry().containsKey(DataKey.BLINK)) {
            blinkSprite = new Sprite(mapkitItem.getTexture(DataKey.BLINK));
        }

        sprite = new Sprite(mapkitItem.getTexture());
        add(sprite);

        setCollisionEnabled(true);
        setWeight(5f);
    }

    @Property
    public void setPickupClassname(String pickupClassname) {
        this.pickupClassname = pickupClassname;
    }

    @Property
    public String getPickupClassname() {
        return pickupClassname;
    }

    @Property
    public void setWeaponClassname(String weaponClassname) {
        this.weaponClassname = weaponClassname;
    }

    @Property
    public String getWeaponClassname() {
        return weaponClassname;
    }

    @Property
    public int getPickupValue() {
        return pickupValue;
    }

    @Property
    public void setPickupValue(int pickupValue) {
        this.pickupValue = pickupValue;
    }

    @Override
    public void setCollisionEnabled(boolean value) {
        collisionEnabled = value;
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
    public void setMaxHealth(int health) {
        this.maxHealth = health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setX(float value) {
        if (value == getX()) return;
        super.setX(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setY(float value) {
        if (value == getY()) return;
        super.setY(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setXY(float x, float y) {
        if (x == getX() && y == getY()) return;
        super.setX(x);
        super.setY(y);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void move(float toX, float toY) {
        if (toX == 0 && toY == 0) return;
        super.moveX(toX);
        super.moveY(toY);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveY(float value) {
        if (value == 0) return;
        super.moveY(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveX(float value) {
        if (value == 0) return;
        super.moveX(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setHealth(int health) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;

        if (health < oldHealth) {
            damageBlink();
            playSound(mapkitItem.getDataEntry().getString(DAMAGE_SOUND));
        }

        this.health = health;
        if (health <= 0) destroy();

        if (isOnWorld()) {
            getWorld().getSyncDataAggregator().health(this, null);
        }
    }

    @Override
    public void setHealthBy(int health, IDamaging damaging, boolean fromServer) {
        int oldHealth = this.health;
        if (health < 0) health = 0;
        else if (health > maxHealth) health = maxHealth;

        if (health < oldHealth) {
            damageBlink();
            playSound(mapkitItem.getDataEntry().getString(DAMAGE_SOUND));
        }

        this.health = health;
        if (health <= 0) destroy();

        if (isOnWorld()) {
            getWorld().getSyncDataAggregator().health(this, damaging);
        }
    }

    @Override
    public void damage(int toHealth, IDamaging damaging) {
        if (toHealth > 0) {
            if (damagingTime > 0) return;
        }

        setHealth(getHealth() - toHealth);
    }

    private void destroy() {
        if (destroyed) return;

        destroyed = true;

        setCollisionEnabled(false);
        sprite.removeFromParent();
        blinkSprite.removeFromParent();
        blinkSprite.setAlpha(1.0f);
        blinkSprite.removeEventListener(blinkSprite);

        if (isOnWorld() && mapkitItem.getDataEntry().containsKey(DataKey.BROKEN_PARTS)) {
            playSound(mapkitItem.getDataEntry().getString(DESTROY_SOUND));

            Texture[] textures = mapkitItem.getTextures(DataKey.BROKEN_PARTS);
            getWorld().getLayer(5).add(Particle.miniExplosionDestroyable(textures, 6, Color.WHITE, 4),
                    getX() + sprite.getWidth() / 2,
                    getY() + sprite.getHeight() / 2
            );
        }

        // drop pickup
        if (isServer()) {
            
        }

    }

    private void damageBlink() {
        damagingTime = DAMAGING_TIME;

        if (blinkSprite != null) {
            blinkSprite.setAlpha(1f);
            add(blinkSprite);
            blinkSprite.addEventListener(blinkSprite, Event.EACH_FRAME, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    blinkSprite.setAlpha(blinkSprite.getAlpha() - 0.1f);
                    if (blinkSprite.getAlpha() <= 0) {
                        blinkSprite.removeEventListener(Event.EACH_FRAME, this);
                        blinkSprite.removeFromParent();
                    }
                }
            });
        }
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void repair() {
        destroyed = false;
        setHealth(getMaxHealth());
        add(sprite);
        setCollisionEnabled(true);

        if (isOnWorld()) getWorld().getSyncDataAggregator().repair(this);
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void process() {
        setFloor(null);
        if (damagingTime > 0) damagingTime--;
    }

    @Override
    public void reset() {
        repair();
        setXY(getStartX(), getStartY());

        if (isOnWorld()) getWorld().getSyncDataAggregator().reset(this);
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {

    }

    @Override
    public boolean isPermanentSync() {
        return true;
    }

    @Override
    public void setFloorOnly(boolean b) {
        floorOnly = b;
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean b) {
        pushable = b;
    }

    @Override
    public boolean isPushable() {
        return pushable;
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
        velocityX = vX;
        velocityY = vY;
    }

    @Override
    public float getVelocityX() {
        return velocityX;
    }

    @Override
    public float getVelocityY() {
        return velocityY;
    }

    @Override
    public void setGravityEnabled(boolean b) {
        gravityEnabled = b;
    }

    @Override
    public boolean isGravityEnabled() {
        return gravityEnabled;
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
        movingSpeedX = value;
    }

    @Override
    public void setMovingSpeedY(float value) {
        movingSpeedY = value;
    }
}
