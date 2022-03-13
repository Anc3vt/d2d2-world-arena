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

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.TouchEvent;
import com.ancevt.d2d2.touch.TouchButton;

public class Title extends Component {

	protected static final String DEFAULT_TITLE_TEXT = "Title";
	
	private static final int PADDING = 8;
	private static final Color BACKGROUND_COLOR = Color.DARK_BLUE;
	private static final Color FOREGROUND_COLOR = Color.WHITE;
	private static final int HEIGHT = 17;
	protected static final Color BORDER_COLOR = Color.LIGHT_GRAY;
	
	private final PlainRect borderLeft, borderRight, borderTop, borderBottom;
	
	private final PlainRect background;
	private final BitmapText label;
	
	private final DisplayObject owner;
	private final TouchButton titleTouchButton;

	private float oldX;
	private float oldY;
	
	public Title(DisplayObject owner) {
		this(owner, DEFAULT_TITLE_TEXT);
	}
	
	public Title(DisplayObject owner, String titleText) {
		this.owner = owner;
		
		background = new PlainRect();
		background.setHeight(HEIGHT);
		background.setColor(BACKGROUND_COLOR);
		label = new BitmapText();
		label.setColor(FOREGROUND_COLOR);
		label.setText(titleText);
		
		label.setX(PADDING);
		label.setY((HEIGHT - label.getBitmapFont().getCharHeight()) / 2.0f);
		
		add(background);
		add(label);
		
		borderLeft = new PlainRect();
		borderRight = new PlainRect();
		borderTop = new PlainRect();
		borderBottom = new PlainRect();

		borderLeft.setSize(1, 1);
		borderRight.setSize(1, 1);
		borderTop.setSize(1, 1);
		borderBottom.setSize(1, 1);
		
		borderLeft.setColor(BORDER_COLOR);
		borderRight.setColor(BORDER_COLOR);
		borderTop.setColor(BORDER_COLOR);
		borderBottom.setColor(BORDER_COLOR);
		
		add(borderLeft);
		add(borderRight);
		add(borderTop);
		add(borderBottom);
		
		titleTouchButton = new TouchButton();
		titleTouchButton.addEventListener(TouchEvent.TOUCH_DOWN, e->{
			TouchEvent touchEvent = (TouchEvent)e;
			oldX = touchEvent.getX();
			oldY = touchEvent.getY();

		});

		titleTouchButton.addEventListener(TouchEvent.TOUCH_DRAG, e->{
			TouchEvent touchEvent = (TouchEvent)e;
			float diffX = touchEvent.getX() - oldX;
			float diffY = touchEvent.getY() - oldY;
			owner.moveX(diffX);
			owner.moveY(diffY);

		});

		titleTouchButton.setEnabled(true);
		add(titleTouchButton);
		
		setHeight(HEIGHT);
	}
	
	public void setWidth(float width) {
		if(background == null) return;
		background.setWidth(width);
		borderTop.setWidth(width);
		borderBottom.setWidth(width);
		borderRight.setX(width);
		
		background.setWidth(width);
		label.setBounds(getWidth() - PADDING * 2, label.getBitmapFont().getCharHeight());
		titleTouchButton.setWidth((int)width);
	}
	
	public void setHeight(float height) {
		if(background == null) return;
		background.setHeight(height);
		borderLeft.setHeight(height);
		borderRight.setHeight(height);
		borderBottom.setY(height);
		titleTouchButton.setHeight((int)height);
	}
	
	public void setSize(float width, float height) {
		if(background == null) return;
		setWidth(width);
		setHeight(height);
	}
	
	@Override
	public float getWidth() {
		if(background == null) return 0f;
		return background.getWidth();
	}
	
	@Override
	public float getHeight() {
		if(background == null) return 0f;
		return background.getHeight();
	}
	
	public void setText(String titleText) {
		label.setText(titleText);
	}
	
	public String getText() {
		return label.getText();
	}
	
}
