package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class ProgressBar extends DisplayObjectContainer {
	
	public static final float DEFAULT_WIDTH = 250.0f;
	public static final float DEFAULT_HEIGHT = 10.0f;
	public static final int DEFAULT_MAX_VALUE = 100;
	public static final int DEFAULT_VALUE = 100;
	public static final Color DEFAULT_BACK_COLOR = Color.DARK_RED;
	public static final Color DEFAULT_FORE_COLOR = Color.RED;
	
	private final PlainRect rectBack;
	private final PlainRect rectFore;
	
	private float maxValue;
	private float value;
	
	public ProgressBar() {
		rectBack = new PlainRect();
		rectFore = new PlainRect();
		
		setBackColor(DEFAULT_BACK_COLOR);
		setForeColor(DEFAULT_FORE_COLOR);
		
		setMaxValue(DEFAULT_MAX_VALUE);
		setValue(DEFAULT_VALUE);
		
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		add(rectBack);
		add(rectFore);
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		update();
	}
	
	public void setValue(float value) {
		if(value > maxValue) value = maxValue; else
		if(value < 0) value = 0;
		
		this.value = value;
		update();
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public float getValue() {
		return value;
	}
	
	public final void setSize(final float width, final float height) {
		rectBack.setSize(width, height);
		rectFore.setHeight(height);
		update();
	}
	
	public final void setBackColor(final Color color) {
		rectBack.setColor(color);
	}
	
	public final void setForeColor(final Color color) {
		rectFore.setColor(color);
	}
	
	public final Color getBackColor() {
		return rectBack.getColor();
	}
	
	public final Color getForeColor() {
		return rectFore.getColor();
	}
	
	@Override
	public float getWidth() {
		return rectFore.getWidth();
	}
	
	@Override
	public float getHeight() {
		return rectFore.getHeight();
	}
	
	protected void update() {
		final float backWidth = rectBack.getWidth();
		final float relValue = value / maxValue;
		rectFore.setWidth(relValue * backWidth);
	}

	public static void main(String[] args) {
		Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

		ProgressBar p = new ProgressBar();
		root.add(p);

		p.setMaxValue(100);
		p.setValue(50);

		D2D2.loop();
	}
}





