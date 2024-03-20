package com.ancevt.d2d2world.editor.ui.mapkitpanel;

import com.ancevt.d2d2.components.Frame;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;

public class MapkitFrame extends Frame {

    private static final float DEFAULT_WIDTH = 240.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private final MapkitPanel mapkitPanel;

    private float oldWidth;
    private float oldHeight;

    public MapkitFrame() {
        mapkitPanel = new MapkitPanel();
        add(mapkitPanel, 0, getTitleHeight());

        setTitle("Mapkit panel");

        addEventListener(getClass(), FrameEvent.RESIZE_START, this::this_resizeStart);
        addEventListener(getClass(), FrameEvent.RESIZE_COMPLETE, this::this_resizeComplete);
        addEventListener(getClass(), InteractiveEvent.UP, this::this_up);

        mapkitPanel.setSize(getWidth(), getHeight() - getTitleHeight());
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_resizeStart(Event event) {
        mapkitPanel.setVisible(false);
    }

    private void this_resizeComplete(Event event) {
        mapkitPanel.setVisible(true);
    }

    private void this_up(Event event) {
        if(oldWidth != getWidth() || oldHeight != getHeight()) {
            mapkitPanel.setSize(getWidth(), getHeight() - getTitleHeight());
            oldWidth = getWidth();
            oldHeight = getHeight();
        }
    }

    public MapkitPanel getMapkitPanel() {
        return mapkitPanel;
    }
}
