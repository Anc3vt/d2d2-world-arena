
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.interactive.TouchButton;

import java.util.ArrayList;
import java.util.List;

public class DropList extends Component {

    private static Texture arrowTexture;

    private static Texture loadArrowTexture() {
        return D2D2.getTextureManager().loadTextureAtlas("component-resources/drop-list-arrow.png").createTexture();
    }

    private static final float PADDING = 5;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    //private static final Color DISABLED_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color FOREGROUND_COLOR = Color.BLACK;

    private static final float DEFAULT_WIDTH = 80;
    private static final float DEFAULT_HEIGHT = 20;

    private final Sprite arrow;
    private final BorderedRect rect;
    private final List<DropListItem> items;
    private final BitmapText label;
    private final TouchButton touchButton;

    private float width;
    private final float height;

    private Object selectedKey;
    private boolean opened;

    private BorderedRect openRect;

    public DropList() {
        if (arrowTexture == null) arrowTexture = loadArrowTexture();

        items = new ArrayList<>();

        rect = new BorderedRect();
        rect.setFillColor(BACKGROUND_COLOR);
        rect.setBorderColor(BORDER_COLOR);
        add(rect);

        arrow = new Sprite(arrowTexture);
        add(arrow);

        label = new BitmapText();
        label.setColor(FOREGROUND_COLOR);
        add(label);

        touchButton = new TouchButton(true);
        touchButton.addEventListener(TouchButtonEvent.DOWN, e -> {
            if (opened) {
                openRect.getParent().remove(openRect);
                opened = false;
                onClose();
            } else
                open();
        });

        add(touchButton);

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;

        redraw();
    }

    public final String getText() {
        return label.getText();
    }

    public final void setText(String value) {
        label.setText(value);
    }

    private void redraw() {
        rect.setSize(width, height);
        arrow.setX(width - arrow.getWidth());
        arrow.setY((height - arrow.getHeight()) / 2);
        label.setX(PADDING);
        label.setY((height - label.getBitmapFont().getCharHeight()) / 2);
        touchButton.setSize(width, height);
    }

    public void select(Object key) {
        for (final DropListItem item : items) {
            if (item.getKey() == items || (item.getKey() != null && item.getKey().equals(key))) {
                selectedKey = item.getKey();
                label.setText(item.getLabelText());
                onSelect(key);
            }
        }
    }

    public void clear() {
        items.clear();
        redraw();
        setText("");
    }

    public void onSelect(Object key) {

    }

    public void onOpen() {

    }

    public void onClose() {

    }

    public Object getSelectedKey() {
        return selectedKey;
    }

    private void open() {
        if (opened) return;

        opened = true;

        openRect = new BorderedRect();
        openRect.setFillColor(BACKGROUND_COLOR);
        openRect.setBorderColor(BORDER_COLOR);

        openRect.setWidth(getWidth());
        openRect.setHeight(DEFAULT_HEIGHT * items.size());

        for (int i = 0; i < items.size(); i++) {
            final DropListItem item = items.get(i);

            final BitmapText bitmapText = new BitmapText();
            bitmapText.setBounds(getWidth(), DEFAULT_HEIGHT);
            bitmapText.setText(item.getLabelText());
            bitmapText.setColor(FOREGROUND_COLOR);
            bitmapText.setX(PADDING);
            bitmapText.setY(i * DEFAULT_HEIGHT + (DEFAULT_HEIGHT - bitmapText.getBitmapFont().getCharHeight()) / 2);
            openRect.add(bitmapText);

            final TouchButton button = new TouchButton(true);
            button.addEventListener(TouchButtonEvent.DOWN, e->{
                select(item.getKey());
                openRect.getParent().remove(openRect);
                opened = false;
                onClose();
            });

            button.setY(i * DEFAULT_HEIGHT);
            button.setSize(getWidth(), DEFAULT_HEIGHT);
            openRect.add(button);
        }

        openRect.setY(DEFAULT_HEIGHT);
        add(openRect);

        onOpen();
    }

    public void setEnabled(boolean value) {
        touchButton.setEnabled(value);
    }

    public boolean isEnabled() {
        return touchButton.isEnabled();
    }

    public void setWidth(int width) {
        this.width = width;
        redraw();
    }

    @Override
    public float getWidth() {
        return rect.getWidth();
    }

    @Override
    public float getHeight() {
        return DEFAULT_HEIGHT;
    }

    public void addItem(DropListItem item) {
        if (items.size() == 0) {
            selectedKey = item.getKey();
            label.setText(item.getLabelText());
        }

        items.add(item);
    }

    public void removeItem(DropListItem item) {
        items.remove(item);
    }

    public int getItemCount() {
        return items.size();
    }

    public DropListItem getItem(int index) {
        return items.get(index);
    }

}
















