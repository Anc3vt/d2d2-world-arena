/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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

public interface IDisplayObjectContainer extends IDisplayObject {

	void add(IDisplayObject child);

	void add(IDisplayObject child, int index);
	
	void add(IDisplayObject child, float x, float y);
	
	void add(IDisplayObject child, int index, float x, float y);
	
	void remove(IDisplayObject child);
	
	int indexOf(IDisplayObject child);

	int getChildCount();

	IDisplayObject getChild(int index);
	
	boolean contains(IDisplayObject child);

	void removeAllChildren();
}