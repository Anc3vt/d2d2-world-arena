
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
