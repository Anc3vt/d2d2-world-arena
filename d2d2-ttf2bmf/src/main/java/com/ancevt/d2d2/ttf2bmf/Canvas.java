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

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

abstract public class Canvas extends JPanel {
	
	private static final int[] availableSizes = {
		8, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208,
		224, 240, 256, 272, 288, 304, 320, 336, 352, 368, 384, 400, 416, 512,
		1024
	};
	
	private static final long serialVersionUID = -2604110502852206660L;
	private static final int MAX_CHARS = 65536;
	
	private String string;
	private Font font;
	private CharInfo[] charInfos;
	
	public Canvas() {
		setDoubleBuffered(true);
		setBackground(java.awt.Color.BLACK);
		charInfos = new CharInfo[MAX_CHARS];
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				//fixSize();
				super.componentResized(e);
			}
		});
	}
	
	private final void fixSize() {
		int width = getWidth();
		int height = getHeight();

		while(!isAvailableSize(width)) width--;
		while(!isAvailableSize(height)) height--;
		
		setSize(width, height);
		
		onSizeFixed();
	}
	
	private static final boolean isAvailableSize(int size) {
		for(int i = 0; i < availableSizes.length; i ++) 
			if(availableSizes[i] == size) return true;
		
		return false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bufferedImage.createGraphics();
		
		final Graphics2D graphics2D = (Graphics2D)g;
		
		if(string == null) return;
//
//		g2.setRenderingHint(
//	        RenderingHints.KEY_TEXT_ANTIALIASING,
//	        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
//	    );

//		graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//
//		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

//		graphics2D.setRenderingHint(
//	        RenderingHints.KEY_TEXT_ANTIALIASING,
//	        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
//	    );

		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(font);
		
		g2.setColor(Color.WHITE);
		
		int x = 0, y = font.getSize();
		
		for(int i = 0; i < string.length(); i ++) {
			
			final char c = string.charAt(i);
			
			final FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
			final int width = fontMetrics.charWidth(c);
			final int height = fontMetrics.getHeight();
			final int toY = fontMetrics.getDescent();

			graphics2D.setColor(Color.WHITE);
			graphics2D.drawString(String.valueOf(c), x, y);

			g2.setFont(font);
			g2.drawString(String.valueOf(c), x, y);
			
			graphics2D.setColor(Color.RED);
			graphics2D.drawRect(x, y - height + toY, width, height);
			
			final CharInfo charInfo = new CharInfo();
			charInfo.character = c;
			charInfo.x = x;
			charInfo.y = y - height + toY;
			charInfo.width = width;
			charInfo.height = height;
			
			charInfos[i] = charInfo;
			
			x += width;
			if(x >= getWidth() - font.getSize()) {
				y += height *(Ttf2Bmf.d2d2WorldSpecial ? 2 : 1);
				x = 0;
			}
		}

		onRedraw(charInfos, bufferedImage);
	}
	
	abstract public void onRedraw(final CharInfo[] charInfos, final BufferedImage bufferedImage);
	abstract public void onSizeFixed();
	
	public final void draw(final String string, final Font font, final int atlasWidth, final int atlasHeight) {
		this.string = string;
		this.font = font;
		setSize(atlasWidth, atlasHeight);
		paintComponent(getGraphics());
	}
}
