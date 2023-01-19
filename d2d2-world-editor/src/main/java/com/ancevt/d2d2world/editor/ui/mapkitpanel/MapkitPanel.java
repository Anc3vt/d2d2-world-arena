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
package com.ancevt.d2d2world.editor.ui.mapkitpanel;

import com.ancevt.d2d2.components.Padding;
import com.ancevt.d2d2.components.Panel;
import com.ancevt.d2d2.components.ScrollPane;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2world.mapkit.Mapkit;

import java.util.ArrayList;
import java.util.List;

public class MapkitPanel extends Panel {

    private final ScrollPane scrollPane;
    private final List<Mapkit> mapkits;
    private final List<MapkitPanelLine> lines;

    private MapkitPanelLine currentLine;

    public MapkitPanel() {
        mapkits = new ArrayList<>();
        lines = new ArrayList<>();

        scrollPane = new ScrollPane();
        scrollPane.setItemHeight(MapkitPanelLine.HEIGHT);
        scrollPane.setPadding(new Padding(MapkitPanelLine.PADDING, MapkitPanelLine.PADDING, MapkitPanelLine.PADDING, MapkitPanelLine.PADDING));
        add(scrollPane);

        addEventListener(getClass(), Event.RESIZE, this::this_resize);

        setPushEventsUp(true);
    }

    private void this_resize(Event event) {
        scrollPane.setSize(getWidth(), getHeight());
        rebuild();
    }

    public void addMapkit(Mapkit mapkit) {
        mapkits.add(mapkit);
        rebuild();
    }

    public void removeMapkit(Mapkit mapkit) {
        mapkits.remove(mapkit);
        rebuild();
    }

    private void clear() {
        scrollPane.clear();
        currentLine = null;
        lines.forEach(MapkitPanelLine::dispose);
        lines.clear();
    }

    private void rebuild() {
        clear();

        mapkits.forEach(mapkit -> {
            mapkit.getItems().forEach(mapkitItem -> {
                if(currentLine == null || !currentLine.hasPlace()) {
                    currentLine = new MapkitPanelLine(getWidth());
                    currentLine.addEventListener(getClass(), InteractiveEvent.WHEEL, scrollPane::dispatchEvent);
                    lines.add(currentLine);
                    scrollPane.addScrollableItem(currentLine);
                }

                currentLine.addMapkitItem(mapkitItem);
            });
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        clear();
    }
}
