
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
