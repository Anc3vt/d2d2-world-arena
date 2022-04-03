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

public class TitledClosablePanel extends TitledPanel {
	
	private Button buttonClose;
	
	public TitledClosablePanel() {
		this(DEFAULT_TITLE_TEXT);
	}
	
	public TitledClosablePanel(String titleText) {
		super(titleText);
		buttonClose = new Button("x") {
			@Override
			public void onButtonPressed() {
				onCloseButtonPressed();
				super.onButtonPressed();
			}
		};
		buttonClose.setSize(26, title.getHeight() - 1);
		buttonClose.setY(-title.getHeight());
		buttonClose.setX(getWidth() - buttonClose.getWidth());
		add(buttonClose);
	}
	
	public void onCloseButtonPressed() {
		
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		
		if(buttonClose != null) {
			buttonClose.setX(getWidth() - buttonClose.getWidth());
		}
	}
}
