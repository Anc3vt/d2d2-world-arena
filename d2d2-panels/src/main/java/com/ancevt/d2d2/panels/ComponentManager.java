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
