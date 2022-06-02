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













