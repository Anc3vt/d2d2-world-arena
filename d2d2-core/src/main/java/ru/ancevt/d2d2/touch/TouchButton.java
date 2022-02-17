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
package ru.ancevt.d2d2.touch;

import ru.ancevt.d2d2.display.DisplayObjectContainer;

public class TouchButton extends DisplayObjectContainer {
	
	private static final int DEFAULT_WIDTH = 1;
	private static final int DEFAULT_HEIGHT = 1;
	
	private final TouchArea touchArea;
	private boolean enabled;
	private boolean dragging;
	
	public TouchButton(final int width, final int height) {
		touchArea = new TouchArea(0, 0, width, height);
		setName("touchButton" + hashCode());
	}
	
	public TouchButton(final int width, final int height, final boolean enabled) {
		this(width, height);
		setEnabled(enabled);
	}
	
	public TouchButton() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public TouchButton(final boolean enabled) {
		this();
		setEnabled(enabled);
	}

	public TouchArea getTouchArea() {
		return touchArea;
	}

	@Override
	public float getWidth() {
		return touchArea.getWidth();
	}
	
	@Override
	public float getHeight() {
		return touchArea.getHeight();
	}
	
	public void setSize(final int w, final int h) {
		touchArea.setUp(0,0,w,h);
	}
	
	public void setSize(final float w, final float h) {
		touchArea.setUp(0,0,(int)w,(int)h);
	}
	
	public void setWidth(final float width) {
		touchArea.setUp(0, 0, (int)width, touchArea.getHeight());
	}
	
	public void setHeight(final float height) {
		touchArea.setUp(0, 0, touchArea.getWidth(), (int)height);
	}
	
	public void setWidth(final int width) {
		touchArea.setUp(0, 0, width, touchArea.getHeight());
	}
	
	public void setHeight(final int height) {
		touchArea.setUp(0, 0, touchArea.getWidth(), height);
	}
	
	@Override
	public void setX(float value) {
		touchArea.setUp((int)value, touchArea.getY(), touchArea.getWidth(), touchArea.getHeight());
		super.setX(value);
	}
	
	@Override
	public void setY(float value) {
		touchArea.setUp(touchArea.getX(), (int)value, touchArea.getWidth(), touchArea.getHeight());
		super.setY(value);
	}
	
	@Override
	public void setXY(float x, float y) {
		setX(x);
		setY(y);
		super.setXY(x, y);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if(this.enabled == enabled) return;
		
		this.enabled = enabled;
		
		final TouchProcessor touchProcessor = TouchProcessor.instance;
		
		if(enabled)
			touchProcessor.registerTouchableComponent(this);
		else
			touchProcessor.unregisterTouchableComponent(this);
	}

	public boolean isDragging() {
		return dragging;
	}

	void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

}
























