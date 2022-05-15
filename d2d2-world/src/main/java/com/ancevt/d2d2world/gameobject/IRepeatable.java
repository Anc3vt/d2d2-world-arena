
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IRepeatable {
	void setRepeat(int repeatX, int repeatY);

	@Property
	void setRepeatX(int repeatX);

	@Property
	void setRepeatY(int repeatY);

	@Property
	int getRepeatX();

	@Property
	int getRepeatY();

	float getOriginalWidth();
	float getOriginalHeight();
}
