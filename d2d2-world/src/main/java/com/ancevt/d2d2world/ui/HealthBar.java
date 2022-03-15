package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.display.Color;

public class HealthBar extends ProgressBar {

    private final float WIDTH = 26;
    private final float HEIGHT = 3;

    public HealthBar() {
        setSize(WIDTH, HEIGHT);
    }

    @Override
    protected void update() {
        super.update();

        int perc = (int) (getValue() / getMaxValue() * 100);

        if (perc > 75) {
            setForeColor(Color.GREEN);
            setBackColor(Color.DARK_GREEN);
        } else if (perc > 50) {
            setForeColor(Color.YELLOW);
            setBackColor(Color.DARK_YELLOW);
        } else if (perc > 25) {
            setForeColor(Color.of(0x964B00));
            setBackColor(Color.of(0x402000));
        } else {
            setForeColor(Color.RED);
            setBackColor(Color.DARK_RED);
        }
    }
}
