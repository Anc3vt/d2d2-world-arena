package com.ancevt.d2d2world.desktop.ui;

import com.ancevt.commons.Pair;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

public class Chooser<T> extends DisplayObjectContainer {

    private static final float DEFAULT_WIDTH = 180;

    private final ArrowButton buttonLeft;
    private final ArrowButton buttonRight;
    private final UiText uiText;
    private final List<Pair<String, T>> items;
    private final Button applyButton;
    private int index;
    private float width;
    private Pair<String, T> selectedItemPair;

    public Chooser() {
        items = new ArrayList<>();

        buttonLeft = new ArrowButton();
        buttonLeft.setDirection(-1);
        buttonLeft.addEventListener(ArrowButton.ArrowButtonEvent.ARROW_BUTTON_PRESS, this::buttonLeft_arrowButtonPress);

        buttonRight = new ArrowButton();
        buttonRight.setDirection(1);
        buttonRight.addEventListener(ArrowButton.ArrowButtonEvent.ARROW_BUTTON_PRESS, this::buttonRight_arrowButtonPress);

        uiText = new UiText();

        applyButton = new Button("Apply");
        applyButton.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, this::applyButton_buttonPressed);

        add(uiText);

        add(buttonLeft);
        add(buttonRight);
        add(applyButton);

        setWidth(DEFAULT_WIDTH);
    }

    private void applyButton_buttonPressed(Event<Button> event) {
        setCurrentItemAsSelected();
    }

    private void setCurrentItemAsSelected() {
        selectedItemPair = items.get(index);
        applyButton.setEnabled(false);
        uiText.setColor(Color.WHITE);
        dispatchEvent(ChooserEvent.builder().type(ChooserEvent.CHOOSER_APPLY).build());
    }

    public void setWidth(float width) {
        this.width = width;
        buttonRight.setX(width - buttonLeft.getWidth() - buttonRight.getWidth());
        applyButton.setX(width + 10);
    }

    @Override
    public float getWidth() {
        return width;
    }

    private void buttonLeft_arrowButtonPress(Event<ArrowButton> event) {
        prev();
    }

    private void buttonRight_arrowButtonPress(Event event) {
        next();
    }

    private void prev() {
        setIndex(getIndex() - 1);

    }

    private void next() {
        setIndex(getIndex() + 1);
    }

    public void setIndex(int index) {
        this.index = index;
        if (this.index <= 0) {
            this.index = 0;
            buttonLeft.setEnabled(false);
        } else {
            buttonLeft.setEnabled(true);
        }

        if (this.index >= items.size() - 1) {
            this.index = items.size() - 1;
            buttonRight.setEnabled(false);
        } else {
            buttonRight.setEnabled(true);
        }

        uiText.setText(items.get(index).getFirst());

        float w = uiText.getTextWidth() + 8;
        uiText.setX((getWidth() - w) / 2);

        applyButton.setEnabled(selectedItemPair != items.get(index));
        uiText.setColor(selectedItemPair == items.get(index) ? Color.LIGHT_GREEN : Color.WHITE);
    }

    public int getIndex() {
        return index;
    }

    public int getItemCount() {
        return items.size();
    }

    public T getSelectedItem() {
        return selectedItemPair.getSecond();
    }

    public void addItem(String key, T item) {
        var pair = Pair.of(key, item);
        items.add(pair);
        selectedItemPair = pair;
        setIndex(items.size() - 1);
    }

    public void dispose() {
        buttonLeft.dispose();
        buttonRight.dispose();
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ChooserEvent extends Event<Chooser<?>> {
        public static final String CHOOSER_APPLY = "chooserApply";
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        D2D2WorldArenaDesktopAssets.load();

        Chooser<String> chooser = new Chooser<>();
        chooser.addItem("windowed", "windowed");
        chooser.addItem("640x480", "640x480");
        chooser.addItem("1920x1080", "1920x1080");

        root.add(chooser);

        D2D2.loop();
    }
}

























