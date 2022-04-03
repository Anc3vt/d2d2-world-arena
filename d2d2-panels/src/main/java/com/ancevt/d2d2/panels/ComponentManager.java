/*
 *   D2D2 Panels
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
package com.ancevt.d2d2.panels;

import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
	
	private static ComponentManager instance;
	
	public static ComponentManager getInstance() {
		return instance == null ? instance = new ComponentManager() : instance;
	}
	
	private final List<Component> components;
	
	private ComponentManager() {
		components = new ArrayList<Component>();
	}
	
	public final void addComponent(Component component) {
		components.add(component);
	}
	
	public final void removeComponent(Component component) {
		components.remove(component);
	}
	
	public final int getComponentCount() {
		return components.size();
	}
	
	public final Component getComponent(int index) {
		return components.get(index);
	}
	
	public final void provideMouseMove(int x, int y) {
		// TODO: implement
	}
}
