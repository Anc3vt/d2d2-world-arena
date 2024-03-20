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
package com.ancevt.d2d2world.editor.objects;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.event.Event;

public class SelectArea extends BorderedRect {

    private static final Color STROKE_COLOR = Color.GRAY;

    public SelectArea() {
        super(10f, 10f);
        setFillColor(null);

        setBorderColor(STROKE_COLOR);

        addEventListener(Event.ENTER_FRAME, e -> setBorderWidth(1 / getAbsoluteScaleX()));
    }

    public void setXY(SelectRectangle selectRectangle) {
        setXY(selectRectangle.getX1(), selectRectangle.getY1());
        setSize(selectRectangle.getWidth(), selectRectangle.getHeight());
    }
}
