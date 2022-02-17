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
package ru.ancevt.d2d2.panels;

public class TitledPanel extends Panel {

	protected static final String DEFAULT_TITLE_TEXT = "Titled panel";
	
	protected final Title title;
	
	public TitledPanel(String titleText) {
		super();
		title = new Title(this, titleText);
		setWidth(DEFAULT_WIDTH);
		add(title);
		title.setY(-title.getHeight());
	}
	
	public void setTitleText(String titleText) {
		title.setText(titleText);
	}
	
	public String getTitleText() {
		return title.getText();
	}
	
	public TitledPanel() {
		this(DEFAULT_TITLE_TEXT);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		
		if (title == null) return;
		
		title.setWidth(width);
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		
		if (title == null) return;
		
		title.setWidth(width);
	}
}













