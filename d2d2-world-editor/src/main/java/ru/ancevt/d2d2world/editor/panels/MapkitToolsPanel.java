/*
 *   D2D2 World Editor
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2world.editor.panels;

import ru.ancevt.d2d2.common.BorderedRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.panels.Button;
import ru.ancevt.d2d2.panels.DropList;
import ru.ancevt.d2d2.panels.DropListItem;
import ru.ancevt.d2d2.panels.Label;
import ru.ancevt.d2d2.panels.TitledPanel;
import ru.ancevt.d2d2world.gameobject.area.Area;
import ru.ancevt.d2d2world.mapkit.AreaMapkit;
import ru.ancevt.d2d2world.mapkit.Mapkit;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.mapkit.MapkitManager;

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
    private static final int ICON_SIZE = 16;

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
        setSize(270, 400);

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
        label.setBounds(180, 30);
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


        List<String> mapkitIds = new ArrayList<>(MapkitManager.getInstance().keySet()).stream().sorted().toList();
        for (String mapkitId : mapkitIds) {
            Mapkit mapkit = MapkitManager.getInstance().get(mapkitId);
            dropListMapkit.addItem(new DropListItem(mapkit.getId(), mapkit));

            for (String mapkitItemId : mapkit.keySet()) {
                MapkitItem mapkitItem = mapkit.getItem(mapkitItemId);

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

        int count = 0;
        int x = 5, y = 5;

        for (Mapkit mapkit : mapkits) {
            for (String mapkitItemId : mapkit.keySet()) {
                MapkitItem mapkitItem = mapkit.getItem(mapkitItemId);

                if ((dropListClass.getSelectedKey() == ALL ||
                        dropListClass.getSelectedKey() == mapkitItem.getGameObjectClass())
                        &&
                        (dropListMapkit.getSelectedKey() == ALL ||
                                dropListMapkit.getSelectedKey() == mapkit)) {

                    final Sprite icon = mapkitItem.getIcon().cloneSprite();

                    fixIconSize(icon);
                    final Button button = new Button() {
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
                        if (y >= 300) break;
                    }
                }
            }
        }

		/*
		for(int i = 0; i < mapkit.getItemCount(); i ++) {
			final MapkitItem mapkitItem = mapkit.getItem(i);
			
			if(Objects.equals(mapkitItem.getCategory(), currentCategory)) {
				count++;
				if(count > 48 * currentPage) {
					
					final Texture iconTexture = mapkitItem.getTexture();
					
					final Sprite icon = new Sprite(iconTexture);
					fixIconSize(icon);
					final Button button = new Button() {
						@Override
						public void onButtonPressed() {
							onMapkitItemSelected(mapkitItem);
							super.onButtonPressed();
						}
					};
					button.setBackgroundColor(Color.WHITE);
					button.setIcon(icon);
					button.setSize(40, 40);
					button.setXY(x, y);
					palette.add(button);
					button.setEnabled(buttonsEnabled);
		
					buttons.add(button);
					
					x += button.getWidth() + 2;
					if(x >= palette.getWidth() - ICON_SIZE) {
						x = 5;
						y += button.getHeight() + 2;
						if (y >= 300) break;
					}
				}
			}
		}

		 */
    }

    private static void fixIconSize(Sprite icon) {
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

    public void setMapkitItem(MapkitItem mapkitItem) {
        label.setText(mapkitItem.getId());
    }

    public void onMapkitItemSelected(MapkitItem mapkitItem) {
        setMapkitItem(mapkitItem);
        dispatchEvent(new MapkitToolsPanelEvent(MapkitToolsPanelEvent.MAPKIT_ITEM_SELECT, this, mapkitItem));
    }
}






















