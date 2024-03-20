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
