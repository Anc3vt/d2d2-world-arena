
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.world.World;

import java.util.HashMap;
import java.util.Map;

public interface IGameObject extends IDisplayObject, IProcessable {

    default Map<String, Object> extra() {
        return DefaultMaps.extraMap.computeIfAbsent(this, k -> new HashMap<>());
    }

    default int tact() {
        return 0;
    }


    default void setGameObjectId(int id) {
        DefaultMaps.gameObjectIdMap.put(this, id);
    }

    default int getGameObjectId() {
        return DefaultMaps.gameObjectIdMap.getOrDefault(this, 0);
    }

    @Property
    String getName();

    @Property
    void setName(String name);

    @Property
    void setX(float x);

    @Property
    float getX();

    @Property
    void setY(float y);

    @Property
    float getY();

    boolean isSavable();

    default void setMapkitItem(MapkitItem mapkitItem) {
        DefaultMaps.mapkitItemMap.put(this, mapkitItem);
    }

    default MapkitItem getMapkitItem() {
        return DefaultMaps.mapkitItemMap.get(this);
    }

    default void setWorld(World world) {
        DefaultMaps.worldMap.putIfAbsent(this, world);
    }

    default World getWorld() {
        return DefaultMaps.worldMap.get(this);
    }

    default boolean isOnWorld() {
        return getWorld() != null;
    }

    default void onAddToWorld(World world) {
        setWorld(world);
    }
}
