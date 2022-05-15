
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IDestroyable extends ICollision, IResettable {

    @Property
    default void setMaxHealth(int health) {
        DefaultMaps.maxHealthsMap.put(this, health);
    }

    @Property
    default int getMaxHealth() {
        return DefaultMaps.maxHealthsMap.getOrDefault(this, 0);
    }

    @Property
    default void setHealth(int health) {
        if(health < 0) {
            health = 0;
        } else if(health > getMaxHealth()) {
            health = getMaxHealth();
        }

        DefaultMaps.healthMap.put(this, health);
    }

    @Property
    default int getHealth() {
        return DefaultMaps.healthMap.getOrDefault(this, 0);
    }

    void repair();

    void setHealthBy(int health, IDamaging damaging, boolean fromServer);

    void damage(int toHealth, IDamaging damaging);
}
