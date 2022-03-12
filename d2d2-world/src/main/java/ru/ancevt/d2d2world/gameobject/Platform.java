package ru.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2world.gameobject.script.ActionProgram;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.world.World;

public abstract class Platform extends Sprite implements IPlatform, IActioned {
    private final MapkitItem mapkitItem;

    private final int gameObjectId;
    private World world;
    private ActionProgram actionProgram;
    private boolean floorOnly;
    private float movingSpeedX, movingSpeedY;
    private float startX, startY;

    @Override
    public void reset() {
        actionProgram.reset();
        setXY(getStartX(), getStartY());
    }

    @Override
    public float getMovingSpeedX() {
        return movingSpeedX;
    }

    public void setMovingSpeedX(float movingSpeedX) {
        this.movingSpeedX = movingSpeedX;
    }

    @Override
    public float getMovingSpeedY() {
        return movingSpeedY;
    }

    public void setMovingSpeedY(float movingSpeedY) {
        this.movingSpeedY = movingSpeedY;
    }

    public Platform(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem.getTexture());
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        actionProgram = ActionProgram.STUB;
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
    public float getStartX() {
        return startX;
    }

    @Override
    public float getStartY() {
        return startY;
    }

    @Override
    public void move(float toX, float toY) {
        movingSpeedX = toX;
        movingSpeedY = toY;
        super.moveX(toX);
        super.moveY(toY);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
    }

    @Override
    public void moveX(float value) {
        movingSpeedX = value;
        super.moveX(value);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
    }

    @Override
    public void moveY(float value) {
        movingSpeedY = value;
        super.moveY(value);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
    }

    @Override
    public void setX(float value) {
        if (value == getX()) return;
        super.setX(value);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
    }

    @Override
    public void setY(float value) {
        if (value == getY()) return;
        super.setY(value);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
    }

    @Override
    public void setXY(float x, float y) {
        if (x == getX() && y == getY()) return;
        super.setX(x);
        super.setY(y);
        if (getWorld() != null) getWorld().getSyncManager().xy(this);
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
    public void setCollisionEnabled(boolean value) {
    }

    @Override
    public boolean isCollisionEnabled() {
        return true;
    }

    @Override
    public void setCollision(float x, float y, float width, float height) {
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
    }

    @Override
    public float getCollisionWidth() {
        return mapkitItem.getTexture().width();
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
    }

    @Override
    public float getCollisionHeight() {
        return mapkitItem.getTexture().height();
    }

    @Override
    public void setCollisionX(float collisionX) {
    }

    @Override
    public float getCollisionX() {
        return 0.0f;
    }

    @Override
    public void setCollisionY(float collisionY) {
    }

    @Override
    public float getCollisionY() {
        return 0.0f;
    }

    @Override
    public void setActionProgram(@NotNull ActionProgram actionProgram) {
        this.actionProgram = actionProgram;
    }

    @Override
    public @NotNull ActionProgram getActionProgram() {
        return actionProgram;
    }

    @Override
    public void process() {

    }

    @Override
    public void onCollide(ICollision collideWith) {

    }

    @Override
    public void setFloorOnly(boolean b) {
        this.floorOnly = b;
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean b) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}























