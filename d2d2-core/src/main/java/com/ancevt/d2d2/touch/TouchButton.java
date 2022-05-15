
package com.ancevt.d2d2.touch;

import com.ancevt.d2d2.display.DisplayObjectContainer;

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
























