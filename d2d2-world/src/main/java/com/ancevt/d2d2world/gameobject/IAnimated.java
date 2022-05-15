
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IAnimated extends IDirectioned, IGameObject {

	@Property
	int getAnimation();

	@Property
	void setAnimation(int animationKey);

	void setAnimation(int animationKey, boolean loop);
}
