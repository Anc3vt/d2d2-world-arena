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
