package com.ancevt.d2d2world.editor.ui.propeditor;

import com.ancevt.d2d2.components.ButtonEx;
import com.ancevt.d2d2.components.ComponentEvent;
import com.ancevt.d2d2.components.FrameManager;
import com.ancevt.d2d2.components.ComponentKit;
import com.ancevt.d2d2.components.Frame;
import com.ancevt.d2d2.components.Padding;
import com.ancevt.d2d2.components.ScrollPane;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.interactive.DragUtil;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.Properties;
import com.ancevt.d2d2world.gameobject.IGameObject;

import java.util.ArrayList;
import java.util.List;

public class PropEditor extends Frame {

    private static final float DEFAULT_WIDTH = 400.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private final ScrollPane scrollPane;
    private final ButtonEx buttonOk;
    private final ButtonEx buttonCancel;
    private final List<PropEditorLine> items;

    public PropEditor(IGameObject gameObject) {
        DragUtil.enableDrag(this);

        items = new ArrayList<>();

        scrollPane = new ScrollPane();

        add(scrollPane, 0, 27);

        scrollPane.setBackgroundVisible(false);
        scrollPane.setItemHeight(26.0f);
        scrollPane.setPadding(new Padding(15, 0, 15, 0));
        scrollPane.setPushEventsUp(false);
        setTitle(gameObject.getClass().getSimpleName() + " " + gameObject.getName());

        buttonOk = ComponentKit.createButtonEx2();
        buttonOk.setText("OK");
        buttonOk.addEventListener(ComponentEvent.ACTION, event -> ok());
        buttonOk.setTabbingEnabled(true);
        add(buttonOk);

        buttonCancel = ComponentKit.createButtonEx2();
        buttonCancel.setText("Cancel");
        buttonCancel.addEventListener(ComponentEvent.ACTION, event -> cancel());
        buttonCancel.setTabbingEnabled(true);
        add(buttonCancel);

        setGameObject(gameObject);

        addEventListener(Event.RESIZE, this::this_resize);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        FrameManager.getInstance().register(this);
    }

    private void ok() {

    }

    private void cancel() {
        dispose();
    }

    public void clear() {
        setTitle("");
        scrollPane.clear();
    }

    private void this_resize(Event event) {
        scrollPane.setSize(getWidth(), getHeight() - scrollPane.getY() - 60);
        buttonOk.setXY(getWidth() / 2.75f - buttonOk.getWidth() / 2f, getHeight() - buttonOk.getHeight() - 13);
        buttonCancel.setXY(getWidth() - getWidth() / 2.75f - buttonOk.getWidth() / 2f, getHeight() - buttonOk.getHeight() - 13);
    }

    private void setGameObject(IGameObject gameObject) {
        DataEntry dataEntry = Properties.getProperties(gameObject);
        dataEntry.getKeyValues().forEach(keyValue -> {
            PropEditorLine propEditorLine = new PropEditorLine(keyValue.key(), keyValue.value());
            items.add(propEditorLine);
            scrollPane.addScrollableItem(propEditorLine);
        });
        scrollPane.setScrollPosition(0);
    }

    @Override
    public void dispose() {
        super.dispose();
        FrameManager.getInstance().unregister(this);
    }
}
