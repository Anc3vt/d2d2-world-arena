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
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;

public class D2D2PanelsDemo {
    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, D2D2PanelsDemo.class.getName() + "(floating)"));

        // Entry point

        final TitledPanel panel = new TitledPanel("My title");

        stage.add(panel);

        panel.setHeight(500);
        panel.setXY(50, 50);

        final Button button = new Button() {
            @Override
            public void onButtonPressed() {
                System.out.println("size: " + stage.getWidth() + "x" + stage.getHeight());
                super.onButtonPressed();
            }
        };
        button.setText("Hello");
        button.setEnabled(false);
        button.setXY(20, 10);
        panel.add(button);

        final Button iconedButton = new Button() {
            @Override
            public void onButtonPressed() {
                setEnabled(false);
                super.onButtonPressed();
            }
        };
        iconedButton.setX(20);
        final Sprite icon = new Sprite("satellite");
        icon.setScale(0.5f, 0.5f);
        iconedButton.setIcon(icon);
        iconedButton.setY(button.getY() + button.getHeight() + 20);
        panel.add(iconedButton);

        final Checkbox checkbox = new Checkbox("This is checkbox") {
            @Override
            public void onCheckedStateChange(boolean checked) {
                button.setEnabled(checked);
                super.onCheckedStateChange(checked);
            }
        };
        checkbox.setXY(20, 100);
        panel.add(checkbox);

        final TextInput textInput = new TextInput();
        textInput.setText("My text");
        panel.add(textInput, 20, 200);

        final TextInput textInput1 = new TextInput() {
            @Override
            public void onTextEnter() {
                if(getText().equals("disable")) {
                    textInput.setEnabled(false);
                } else
                if(getText().equals("enable")) {
                    textInput.setEnabled(true);
                }
                super.onTextEnter();
            }
        };
        textInput1.setText("My text");
        panel.add(textInput1, 20, 230);

        final DropList dropList = new DropList();
        dropList.setWidth(100);
        dropList.addItem(new DropListItem("Item #1", "Key #1"));
        dropList.addItem(new DropListItem("Item #2", "Key #2"));
        dropList.addItem(new DropListItem("Item #3", "Key #3"));
        dropList.addItem(new DropListItem("Item #4", "Key #4"));

        dropList.clear();
        dropList.addItem(new DropListItem("Item #5", "Key #5"));
        dropList.addItem(new DropListItem("Item #6", "Key #6"));
        dropList.addItem(new DropListItem("Item #7", "Key #7"));
        dropList.addItem(new DropListItem("Item #8", "Key #8"));




        panel.add(dropList, 10, 300);

        final FpsMeter fpsMeter = new FpsMeter();
        fpsMeter.setXY(0, 0);
        stage.add(fpsMeter);

        D2D2.loop();
    }
}
