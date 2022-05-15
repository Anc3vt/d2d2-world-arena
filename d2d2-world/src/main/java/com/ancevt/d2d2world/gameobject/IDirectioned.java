
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IDirectioned extends IGameObject{

	@Property
	default void setDirection(int direction) {
		DefaultMaps.directionedMap.put(this, direction);
	}

	@Property
	default int getDirection() {
		return DefaultMaps.directionedMap.getOrDefault(this, 0);
	}
}
