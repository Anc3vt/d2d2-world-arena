/*
 *   D2D2 Panels
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
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.common.IDisposable;
import com.ancevt.d2d2.display.DisplayObjectContainer;

public class Component extends DisplayObjectContainer implements IDisposable {

	private boolean focused;
	private boolean disposed;
	
	public Component() {
		ComponentManager.getInstance().addComponent(this);
	}
	
	public void setFocused(boolean value) {
		this.focused = value;
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public void onFocus() {
		
	}
	
	public void onFocusLost() {
		
	}

	@Override
	public void dispose() {
		ComponentManager.getInstance().removeComponent(this);
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}
}
