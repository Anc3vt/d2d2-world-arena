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

package com.ancevt.d2d2.ttf2bmf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

abstract class Window extends JFrame {

	private static final long serialVersionUID = -8806222528170476354L;

	private final Canvas canvas;
	
	public Window(int width, int height) {
		super();
		
		canvas = new Canvas() {
			private static final long serialVersionUID = 1312762434406648138L;

			@Override
			public void onRedraw(CharInfo[] charInfos, BufferedImage bufferedImage) {
				Window.this.onRedraw(charInfos, bufferedImage);
			}
			
			@Override
			public void onSizeFixed() {
				System.out.println(getWidth() + "x" + getHeight());
				
			}
		};
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setBackground(Color.BLACK);
		
		add(canvas);
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	abstract public void onRedraw(CharInfo[] charInfos, BufferedImage bufferedImage);
	
	
}
