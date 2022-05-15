
package com.ancevt.d2d2.debug;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;

public class FpsMeter extends BitmapText {

	private long time = System.currentTimeMillis();
	private int frameCounter;
	private int actualFramesPerSeconds;

	public FpsMeter(BitmapFont font) {
		super(font);
		setName("fpsMeter");
		addEventListener(Event.EACH_FRAME, this::eachFrame);
	}

	public FpsMeter() {
		this(BitmapFont.getDefaultBitmapFont());
	}
	
	public final int getFramesPerSecond() {
		return actualFramesPerSeconds;
	}

	public void eachFrame(Event event) {

		frameCounter++;
		final long time2 = System.currentTimeMillis();

		if (time2 - time >= 1000) {
			time = System.currentTimeMillis();

			setText("FPS: " + frameCounter);
			actualFramesPerSeconds = frameCounter;

			if (frameCounter > 40)
				setColor(Color.GREEN);
			else if (frameCounter >= 30 && frameCounter < 40)
				setColor(Color.YELLOW);
			else if (frameCounter < 30)
				setColor(Color.RED);

			frameCounter = 0;
		}
	}

	@Override
	public String toString() {
		return "FPSMeter{" +
				"time1=" + time +
				", frameCounter=" + frameCounter +
				", actualFramesPerSeconds=" + actualFramesPerSeconds +
				'}';
	}
}

