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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

abstract public class Platform extends DisplayObjectContainer implements IPlatform, IActioned {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    private String actionProgramData = ";";
    private ActionProgram actionProgram;
    private boolean floorOnly;
    private float movingSpeedX, movingSpeedY;
    private float startX, startY;
    private boolean permanentSync;
    private World world;

    public Platform(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
        actionProgram = ActionProgram.STUB;
        setPermanentSync(true);
    }

    @Override
    public boolean isPermanentSync() {
        return permanentSync;
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {
        this.permanentSync = permanentSync;
    }

    @Override
    public void reset() {
        actionProgram.reset();
        setXY(getStartX(), getStartY());
    }

    @Override
    public float getMovingSpeedX() {
        var v = movingSpeedX;
        movingSpeedX = 0;
        return v;
    }

    public void setMovingSpeedX(float movingSpeedX) {
        this.movingSpeedX = movingSpeedX;
    }

    @Override
    public float getMovingSpeedY() {
        var v = movingSpeedY;
        movingSpeedY = 0;
        return v;
    }

    public void setMovingSpeedY(float movingSpeedY) {
        this.movingSpeedY = movingSpeedY;
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
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveX(float value) {
        movingSpeedX = value;
        super.moveX(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveY(float value) {
        movingSpeedY = value;
        super.moveY(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
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
    public void setActionProgramData(@NotNull String actionProgramData) {
        this.actionProgramData = actionProgramData;
        actionProgram = ActionProgram.parse(this, actionProgramData);
    }

    @Override
    public @NotNull String getActionProgramData() {
        return actionProgramData;
    }

    @NotNull
    @Override
    public ActionProgram getActionProgram() {
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























