
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface ICollision extends IGameObject {

    @Property
    default void setCollisionEnabled(boolean value) {
        DefaultMaps.collisionEnabledMap.put(this, value);
    }

    @Property
    default boolean isCollisionEnabled() {
        return DefaultMaps.collisionEnabledMap.getOrDefault(this, true);
    }

    default void setCollision(float x, float y, float width, float height) {
        setCollisionX(x);
        setCollisionY(y);
        setCollisionWidth(width);
        setCollisionHeight(height);
    }

    @Property
    default void setCollisionWidth(float collisionWidth) {
        DefaultMaps.collisionWidthMap.put(this, collisionWidth);
    }

    @Property
    default float getCollisionWidth() {
        return DefaultMaps.collisionWidthMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionHeight(float collisionHeight) {
        DefaultMaps.collisionHeightMap.put(this, collisionHeight);
    }

    @Property
    default float getCollisionHeight() {
        return DefaultMaps.collisionHeightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionX(float collisionX) {
        DefaultMaps.collisionXMap.put(this, collisionX);
    }

    @Property
    default float getCollisionX() {
        return DefaultMaps.collisionXMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionY(float collisionY) {
        DefaultMaps.collisionYMap.put(this, collisionY);
    }

    @Property
    default float getCollisionY() {
        return DefaultMaps.collisionYMap.getOrDefault(this, 0f);
    }

    default void onCollide(ICollision collideWith) {

    }

}
