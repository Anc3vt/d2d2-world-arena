
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;

public class Label extends BitmapText {
	
	public Label(String label) {
		this();
		setText(label);
	}
	
	public Label() {
		setColor(Color.BLACK);
	}
}
