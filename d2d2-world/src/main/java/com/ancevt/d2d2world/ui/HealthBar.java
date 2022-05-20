
package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.display.Color;

public class HealthBar extends Bar {

    private final float WIDTH = 26f;
    private final float HEIGHT = 2f;

    public HealthBar() {
        setSize(WIDTH, HEIGHT);
    }

    @Override
    protected void update() {
        super.update();

        int perc = (int) (getValue() / getMaxValue() * 100);

        if (perc > 75) {
            setForeColor(Color.GREEN);
            setBackColor(Color.of(0x002200));
        } else if (perc > 50) {
            setForeColor(Color.YELLOW);
            setBackColor(Color.of(0x222200));
        } else if (perc > 25) {
            setForeColor(Color.of(0x964B00));
            setBackColor(Color.of(0x111100));
        } else {
            setForeColor(Color.RED);
            setBackColor(Color.of(0x220000));
        }
    }
}
