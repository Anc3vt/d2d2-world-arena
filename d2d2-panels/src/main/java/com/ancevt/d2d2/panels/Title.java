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

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveContainer;

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
	private final InteractiveContainer titleTouchButton;

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
		
		titleTouchButton = new InteractiveContainer();
		titleTouchButton.addEventListener(InteractiveEvent.DOWN, e->{
			InteractiveEvent touchButtonEvent = (InteractiveEvent)e;
			oldX = touchButtonEvent.getX();
			oldY = touchButtonEvent.getY();

		});

		titleTouchButton.addEventListener(InteractiveEvent.DRAG, e->{
			InteractiveEvent touchButtonEvent = (InteractiveEvent)e;
			float diffX = touchButtonEvent.getX() - oldX;
			float diffY = touchButtonEvent.getY() - oldY;
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
