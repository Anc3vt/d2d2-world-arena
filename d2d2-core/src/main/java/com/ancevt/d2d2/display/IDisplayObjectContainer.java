/*
 *   D2D2 core
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
