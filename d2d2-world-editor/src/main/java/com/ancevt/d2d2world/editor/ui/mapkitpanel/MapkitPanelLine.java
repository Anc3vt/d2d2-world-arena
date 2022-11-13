package com.ancevt.d2d2world.editor.ui.mapkitpanel;

import com.ancevt.d2d2.components.ButtonEx;
import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.components.Tooltip;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.mapkit.MapkitItem;

import java.util.ArrayList;
import java.util.List;

public class MapkitPanelLine extends Component {

    public static final float HEIGHT = 50.0f;
    public static final float PADDING = 5.0f;

    private static final float BUTTON_WIDTH = 40.0f;
    private static final float BUTTON_HEIGHT = 40.0f;

    private final List<ButtonEx> buttons;

    public MapkitPanelLine(float width) {
        buttons = new ArrayList<>();

        setSize(width, HEIGHT);

        setPushEventsUp(true);
    }

    public boolean hasPlace() {
        return (buttons.size() + 1) * (BUTTON_WIDTH + PADDING) < getWidth();
    }

    public void addMapkitItem(MapkitItem mapkitItem) {
        ButtonEx button = new ButtonEx();
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setIcon(mapkitItem.getIcon().getTexture());

        Tooltip tooltip = Tooltip.createTooltip();
        tooltip.setImageBackgroundVisible(true);
        tooltip.setMaxImageSize(100f, 100f);
        tooltip.setImageScale(3);
        tooltip.setTexture(mapkitItem.getIcon().getTexture());

        DataEntry d = mapkitItem.getDataEntry();

        String id = d.getString(DataKey.ID);
        String cls = d.getString(DataKey.CLASS);
        String atlas = d.containsKey(DataKey.ATLAS) ? d.getString(DataKey.ATLAS) : "";

        tooltip.setText("#<FFFFFF>%s\n\n<888800>%s\n\n<888888>%s".formatted(id, cls, atlas));
        button.setTooltip(tooltip);
        button.addEventListener(getClass(), InteractiveEvent.WHEEL, this::dispatchEvent);

        add(button);

        button.setX((button.getWidth() + PADDING) * buttons.size());

        buttons.add(button);
    }


    @Override
    public void dispose() {
        super.dispose();
        buttons.forEach(Component::dispose);
    }
}
