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
package com.ancevt.d2d2world.editor.ui.propeditor;

import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.components.ComponentKit;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.components.TextInput;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;

public class PropEditorLine extends Component {

    private final float HEIGHT = 18f;

    private final BitmapText bitmapText;
    private final TextInput textInput;
    private final Object value;

    public PropEditorLine(String propertyName, Object value) {
        this.value = value;
        bitmapText = new BitmapText();
        bitmapText.setColor(Color.GRAY);
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        bitmapText.setText(getReadablePropertyName(propertyName));
        bitmapText.setAutosize(true);
        add(bitmapText);

        textInput = ComponentKit.createTextInput2();
        textInput.addEventListener(PropEditorLine.class, InteractiveEvent.FOCUS_IN, this::textInput_focusIn);
        textInput.addEventListener(PropEditorLine.class, InteractiveEvent.FOCUS_OUT, this::textInput_focusOut);
        textInput.setText("" + value);
        add(textInput);

        addEventListener(Event.RESIZE, this::this_resize);

        setHeight(HEIGHT);
    }

    private void textInput_focusIn(Event event) {
        bitmapText.setColor(Color.WHITE);
    }

    private void textInput_focusOut(Event event) {
        bitmapText.setColor(Color.GRAY);
    }

    private String getReadablePropertyName(String propertyName) {
        return propertyName;
    }

    private void this_resize(Event event) {
        bitmapText.setXY(0, (getHeight() - bitmapText.getTextHeight()) / 2 + 1);
        textInput.setWidth(getWidth() / 2);
        textInput.setXY(getWidth() - textInput.getWidth() - 1, (getHeight() - textInput.getHeight()) / 2);
    }

    public Object getValue() {
        return value;
    }
}
