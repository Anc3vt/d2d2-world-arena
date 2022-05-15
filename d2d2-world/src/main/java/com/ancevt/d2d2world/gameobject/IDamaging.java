
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IDamaging extends ICollision {

	@Property
	default void setDamagingPower(int damagingPower) {
		DefaultMaps.damagingPowerMap.put(this, damagingPower);
	}

	@Property
	default int getDamagingPower() {
		return DefaultMaps.damagingPowerMap.getOrDefault(this, 0);
	}

	default void setDamagingOwnerActor(Actor actor) {
		DefaultMaps.damagingOwnerActorMap.put(this, actor);
	}

    default Actor getDamagingOwnerActor() {
		return DefaultMaps.damagingOwnerActorMap.get(this);
	}
}
