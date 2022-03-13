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

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.TouchEvent;
import com.ancevt.d2d2.touch.TouchButton;

public class Checkbox extends Component {
	
	private static final float PADDING = 8;
	private static final float BOX_OUTER_SIZE = 16;
	private static final float BOX_INNER_SIZE = 6;
	private static final Color BOX_OUTER_DISABLED_FILL_COLOR = Color.GRAY;
	private static final Color BOX_OUTER_FILL_COLOR = Color.WHITE;
	private static final Color BOX_OUTER_BORDER_COLOR = Color.BLACK;
	private static final Color BOX_INNER_COLOR = Color.BLACK;
	private static final Color LABEL_COLOR = Color.BLACK;
	private static final String DEFAULT_LABEL_TEXT = "Checkbox";
	private static final float DEFAULT_WIDTH = 200;
	
	private final BorderedRect boxOuter;
	private final PlainRect boxInner;
	private final BitmapText label;
	private final TouchButton touchButton;
	
	private boolean checked;
	
	public Checkbox() {
		this(DEFAULT_LABEL_TEXT);
	}
	
	public Checkbox(String labelText) {
		boxOuter = new BorderedRect(BOX_OUTER_SIZE, BOX_OUTER_SIZE, BOX_OUTER_FILL_COLOR, BOX_OUTER_BORDER_COLOR);
		add(boxOuter);
		
		boxInner = new PlainRect(BOX_INNER_SIZE, BOX_INNER_SIZE, BOX_INNER_COLOR);
		boxInner.setXY((BOX_OUTER_SIZE - BOX_INNER_SIZE)/2, (BOX_OUTER_SIZE - BOX_INNER_SIZE)/2);
		
		label = new BitmapText();
		label.setBoundWidth(DEFAULT_WIDTH - BOX_OUTER_SIZE - PADDING);
		label.setText(labelText);
		label.setColor(LABEL_COLOR);
		label.setX(boxOuter.getWidth() + PADDING);
		label.setY((boxOuter.getHeight() - label.getBitmapFont().getCharHeight() * label.getAbsoluteScaleY()) / 2);
		add(label);
		
		touchButton = new TouchButton();
		touchButton.addEventListener(TouchEvent.TOUCH_DOWN, e->{
			setChecked(!isChecked());

			Focus.setFocusedComponent(this);

			if(checked) {
				add(boxInner);
			} else {
				remove(boxInner);
			}
		});

		touchButton.setEnabled(true);
		touchButton.setSize(boxOuter.getWidth() + PADDING + label.getWidth(), boxOuter.getHeight());
		add(touchButton);
	}
	
	public void setWidth(float width) {
		label.setBoundWidth(width - BOX_OUTER_SIZE - PADDING);
		touchButton.setSize(boxOuter.getWidth() + PADDING + label.getWidth(), boxOuter.getHeight());
	}
	
	@Override
	public float getWidth() {
		return boxOuter.getWidth() + PADDING + label.getWidth();
	}
	
	@Override
	public float getHeight() {
		return boxOuter.getHeight();
	}
	
	public void onCheckedStateChange(boolean checked) {
		
	}
	
	public final void setLabelText(String value) {
		label.setText(value);
	}
	
	public String getLabelText() {
		return label.getText();
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		if(this.checked != checked)
			onCheckedStateChange(checked);
		
		this.checked = checked;
	}
	
	public void setEnabled(boolean value) {
		touchButton.setEnabled(value);
		
		boxOuter.setFillColor(value ? BOX_OUTER_FILL_COLOR : BOX_OUTER_DISABLED_FILL_COLOR);
	}

	public boolean isEnabled() {
		return touchButton.isEnabled();
	}
}













