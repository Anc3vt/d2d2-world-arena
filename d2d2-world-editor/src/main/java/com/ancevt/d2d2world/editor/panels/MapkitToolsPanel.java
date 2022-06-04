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
package com.ancevt.d2d2world.editor.panels;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.panels.Button;
import com.ancevt.d2d2.panels.DropList;
import com.ancevt.d2d2.panels.DropListItem;
import com.ancevt.d2d2.panels.Label;
import com.ancevt.d2d2.panels.TitledPanel;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.area.Area;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapkitToolsPanel extends TitledPanel {

    private static final MapkitToolsPanel instance = new MapkitToolsPanel();

    public static MapkitToolsPanel getInstance() {
        return instance;
    }

    private final static Object ALL = new Object();

    private static final String TITLE = "Mapkit tools";
    private static final int ICON_SIZE = 48;

    private final Label label;
    private final DropList dropListClass;
    private final DropList dropListMapkit;
    private final List<Button> buttons;
    private final BorderedRect palette;
    private int currentPage;
    private boolean buttonsEnabled;

    private final List<Mapkit> mapkits;

    private MapkitToolsPanel() {
        buttonsEnabled = true;

        buttons = new ArrayList<>();

        setTitleText(TITLE);
        setSize(270, 65536);

        palette = new BorderedRect(260, getHeight() - 100, Color.WHITE, Color.BLACK);
        palette.setXY(5, 50);


        Button pagePrev = new Button("<") {
            @Override
            public void onButtonPressed() {
                prevPage();
            }
        };
        pagePrev.setSize(30, 30);
        add(pagePrev, 5, palette.getY() + palette.getHeight() + 5);
        Button pageNext = new Button(">") {
            @Override
            public void onButtonPressed() {
                nextPage();
            }
        };
        pageNext.setSize(30, 30);
        add(pageNext, 40, palette.getY() + palette.getHeight() + 5);

        label = new Label("Label");
        label.setSize(180, 30);
        add(label, 80, palette.getY() + palette.getHeight() + 5);

        dropListClass = new DropList() {
            @Override
            public void onClose() {
                setAllButtonsEnabled(true);
                dropListMapkit.setEnabled(true);
            }

            @Override
            public void onOpen() {
                setAllButtonsEnabled(false);
                dropListMapkit.setEnabled(false);
            }

            @Override
            public void onSelect(Object key) {
                viewPage(currentPage);
            }
        };
        dropListClass.setWidth(260);


        dropListMapkit = new DropList() {
            @Override
            public void onClose() {
                setAllButtonsEnabled(true);
                dropListClass.setEnabled(true);
            }

            @Override
            public void onOpen() {
                setAllButtonsEnabled(false);
                dropListClass.setEnabled(false);
            }

            @Override
            public void onSelect(Object key) {
                viewPage(currentPage);
            }
        };
        dropListMapkit.setWidth(260);

        add(palette);
        add(dropListClass, 5, 28);
        add(dropListMapkit, 5, 5);

        mapkits = new ArrayList<>();
    }

    public void addMapkit(Mapkit mapkit) {
        mapkits.add(mapkit);
        mapkits.sort((o1, o2) -> (o1 instanceof AreaMapkit) ? -1 : 0);


        updateMapkits();
    }

    public void removeMapkit(Mapkit mapkit) {
        mapkits.remove(mapkit);
        updateMapkits();
    }

    public void updateMapkits() {
        dropListMapkit.clear();
        dropListClass.clear();

        dropListMapkit.addItem(new DropListItem("All", ALL));
        dropListClass.addItem(new DropListItem("All", ALL));

        Set<Class<?>> classes = new HashSet<>();

        List<String> mapkitNames = new ArrayList<>(MapkitManager.getInstance().keySet()).stream().sorted().toList();
        for (String mapkitName : mapkitNames) {
            Mapkit mapkit = MapkitManager.getInstance().getMapkit(mapkitName);
            dropListMapkit.addItem(new DropListItem(mapkit.getName(), mapkit));

            for (var mapkitItem : mapkit.getItems()) {
                Class<?> clazz = mapkitItem.getGameObjectClass();

                if (clazz.getSuperclass() != Area.class && !classes.contains(clazz)) {
                    String label = clazz.getSimpleName();
                    dropListClass.addItem(new DropListItem(label, clazz));
                }

                classes.add(clazz);
            }
        }

        viewPage(currentPage);
    }

    private void nextPage() {
        viewPage(++currentPage);
    }

    private void prevPage() {
        if (currentPage == 0) return;
        viewPage(--currentPage);
    }

    private void setAllButtonsEnabled(boolean value) {
        this.buttonsEnabled = value;
        for (final Button b : buttons) {
            b.setEnabled(value);
        }
    }

    private void viewPage(int pageNumber) {
        while (!buttons.isEmpty()) {
            buttons.remove(0).removeFromParent();
        }

        setTitleText(TITLE + " (" + currentPage + ")");

        List<MapkitItem> items = new ArrayList<>();

        for (Mapkit mapkit : mapkits) {
            for (var mapkitItem : mapkit.getItems()) {
                if (mapkitItem.getGameObjectClass() == PlayerActor.class ||
                        mapkitItem.getId().startsWith("bullet_of_")) continue;

                if ((dropListClass.getSelectedKey() == ALL ||
                        dropListClass.getSelectedKey() == mapkitItem.getGameObjectClass())
                        &&
                        (dropListMapkit.getSelectedKey() == ALL || dropListMapkit.getSelectedKey() == mapkit)) {

                    items.add(mapkitItem);
                }
            }
        }

        float w = palette.getWidth() / ICON_SIZE;
        float h = palette.getHeight() / ICON_SIZE;

        int PAGE_SIZE = (int) (w * h);
        int x = 5, y = 5;

        for (int i = PAGE_SIZE * currentPage; i < items.size() && i < currentPage * PAGE_SIZE + PAGE_SIZE * 2; i++) {
            MapkitItem mapkitItem = items.get(i);

            Sprite icon = mapkitItem.getIcon().cloneSprite();

            fixIconSize(icon);
            Button button = new Button() {
                @Override
                public void onButtonPressed() {
                    onMapkitItemSelected(mapkitItem);
                    super.onButtonPressed();
                }
            };
            button.setBackgroundColor(Color.WHITE);
            button.setIcon(icon);
            button.setSize(ICON_SIZE, ICON_SIZE);
            button.setXY(x, y);
            palette.add(button);
            button.setEnabled(buttonsEnabled);
            buttons.add(button);

            x += button.getWidth() + 2;
            if (x >= palette.getWidth() - ICON_SIZE) {
                x = 5;
                y += button.getHeight() + 2;
                //if (y >= 300) break;
            }
        }
    }

    private static void fixIconSize(@NotNull Sprite icon) {
        while (icon.getWidth() * icon.getScaleX() < ICON_SIZE || icon.getHeight() * icon.getScaleY() < ICON_SIZE) {
            icon.setScale(
                    icon.getScaleX() * 1.1f,
                    icon.getScaleY() * 1.1f
            );
        }

        while (icon.getWidth() * icon.getScaleX() > ICON_SIZE || icon.getHeight() * icon.getScaleY() > ICON_SIZE) {
            icon.setScale(
                    icon.getScaleX() * 0.9f,
                    icon.getScaleY() * 0.9f
            );
        }
    }

    public void setMapkitItem(@NotNull MapkitItem mapkitItem) {
        label.setText(mapkitItem.getId());
    }

    public void onMapkitItemSelected(MapkitItem mapkitItem) {
        setMapkitItem(mapkitItem);
        dispatchEvent(MapkitToolsPanelEvent.builder()
                .type(MapkitToolsPanelEvent.MAPKIT_ITEM_SELECT)
                .mapkitItem(mapkitItem)
                .build());
    }
}






















