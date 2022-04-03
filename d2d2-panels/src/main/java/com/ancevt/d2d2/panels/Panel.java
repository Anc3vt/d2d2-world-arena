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

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;

public class Panel extends Component {
   
	protected static final float DEFAULT_WIDTH = 200;
	protected static final float DEFAULT_HEIGHT = 100;
	
	private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
	protected static final Color BORDER_COLOR_1 = Color.WHITE;
	protected static final Color BORDER_COLOR_2 = Color.DARK_GRAY;
	
	protected final PlainRect borderLeft, borderRight, borderTop, borderBottom;
	protected final PlainRect background;
	
	public Panel() {
		borderLeft = new PlainRect();
		borderRight = new PlainRect();
		borderTop = new PlainRect();
		borderBottom = new PlainRect();
		background = new PlainRect();

		borderLeft.setSize(1, 1);
		borderRight.setSize(1, 1);
		borderTop.setSize(1, 1);
		borderBottom.setSize(1, 1);
		background.setSize(1, 1);
		
		borderLeft.setColor(BORDER_COLOR_1);
		borderRight.setColor(BORDER_COLOR_2);
		borderTop.setColor(BORDER_COLOR_1);
		borderBottom.setColor(BORDER_COLOR_2);
		background.setColor(BACKGROUND_COLOR);
		
		add(background);
		add(borderLeft);
		add(borderRight);
		add(borderTop);
		add(borderBottom);
		
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public void setWidth(float width) {
		background.setWidth(width);
		borderTop.setWidth(width);
		borderBottom.setWidth(width);
		borderRight.setX(width);
	}
	
	public void setHeight(float height) {
		background.setHeight(height);
		borderLeft.setHeight(height);
		borderRight.setHeight(height);
		borderBottom.setY(height);
	}
	
	public void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}
	
	@Override
	public float getWidth() {
		return background.getWidth();
	}
	
	@Override
	public float getHeight() {
		return background.getHeight();
	}
	
	
	
}











