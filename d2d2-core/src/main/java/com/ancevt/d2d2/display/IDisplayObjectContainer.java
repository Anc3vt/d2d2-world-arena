
package com.ancevt.d2d2.display;

import org.jetbrains.annotations.NotNull;

public interface IDisplayObjectContainer extends IDisplayObject {

	void add(@NotNull IDisplayObject child);

	void add(@NotNull IDisplayObject child, int index);
	
	void add(@NotNull IDisplayObject child, float x, float y);
	
	void add(@NotNull IDisplayObject child, int index, float x, float y);
	
	void remove(@NotNull IDisplayObject child);
	
	int indexOf(@NotNull IDisplayObject child);

	int getChildCount();

	@NotNull IDisplayObject getChild(int index);
	
	boolean contains(@NotNull IDisplayObject child);

	void removeAllChildren();
}
