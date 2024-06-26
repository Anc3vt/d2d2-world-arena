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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Stage;

public class Bar extends Container {

    private static final float DEFAULT_WIDTH = 250.0f;
    private static final float DEFAULT_HEIGHT = 10.0f;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE = 100;
    private static final Color DEFAULT_BACK_COLOR = Color.DARK_GREEN;
    private static final Color DEFAULT_FORE_COLOR = Color.GREEN;
    private static final float LIGHT_SHADOW_LINES_HEIGHT = 0.2f;

    private final PlainRect rectBack;
    private final PlainRect rectFore;
    private final PlainRect lightLine;
    private final PlainRect shadowLine;

    private float maxValue;
    private float value;

    public Bar() {
        rectBack = new PlainRect();
        rectFore = new PlainRect();

        lightLine = new PlainRect(Color.WHITE);
        shadowLine = new PlainRect(Color.BLACK);

        lightLine.setAlpha(0.5f);
        lightLine.setScaleY(0.5f);

        setBackColor(DEFAULT_BACK_COLOR);
        setForeColor(DEFAULT_FORE_COLOR);

        setMaxValue(DEFAULT_MAX_VALUE);
        setValue(DEFAULT_VALUE);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        add(rectBack);
        add(rectFore);
        add(lightLine);
        add(shadowLine);
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        update();
    }

    public void setValue(float value) {
        if (value > maxValue) value = maxValue;
        else if (value < 0) value = 0;

        this.value = value;
        update();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getValue() {
        return value;
    }

    public final void setSize(final float width, final float height) {
        rectBack.setSize(width, height);
        rectFore.setHeight(height);
        lightLine.setSize(width, LIGHT_SHADOW_LINES_HEIGHT);
        shadowLine.setSize(width, LIGHT_SHADOW_LINES_HEIGHT);
        shadowLine.setY(height - LIGHT_SHADOW_LINES_HEIGHT);
        update();
    }

    public final void setBackColor(final Color color) {
        rectBack.setColor(color);
    }

    public final Color getBackColor() {
        return rectBack.getColor();
    }

    public final void setForeColor(final Color color) {
        rectFore.setColor(color);
    }

    public final Color getForeColor() {
        return rectFore.getColor();
    }

    @Override
    public float getWidth() {
        return rectFore.getWidth();
    }

    @Override
    public float getHeight() {
        return rectFore.getHeight();
    }

    protected void update() {
        final float backWidth = rectBack.getWidth();
        final float relValue = value / maxValue;
        rectFore.setWidth(relValue * backWidth);
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        stage.setBackgroundColor(Color.GRAY);

        Bar p = new Bar();
        p.setSize(26, 3);

        p.setScale(15f, 15f);

        stage.add(p, 100, 100);
        p.setMaxValue(100);
        p.setValue(75f);

        D2D2.loop();
    }
}





