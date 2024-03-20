/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.common.IDisposable;
import com.ancevt.d2d2.display.Container;

public class Component extends Container implements IDisposable {

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
