package com.ancevt.d2d2.demo;

import com.ancevt.commons.Holder;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;

import static java.lang.Float.parseFloat;

public class D2D2Demo_Micropixel {

    private static final String PROPERTY_NAME = "micropixel.factor";

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        root.setBackgroundColor(Color.GRAY);

        TestObject testObject = new TestObject();
        root.add(testObject);

        testObject.setScale(15,15);

        DebugPanel.setEnabled(true);
        DebugPanel.show(PROPERTY_NAME, 0.05f);

        Holder<Float> factorHolder = new Holder<>(parseFloat(System.getProperty(PROPERTY_NAME)));

        root.addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;

            float factor = factorHolder.getValue();

            switch (e.getKeyCode()) {
                case KeyCode.W -> {
                    testObject.pr2.moveY(-factor);
                }
                case KeyCode.S -> {
                    testObject.pr2.moveY(factor);
                }
                case KeyCode.UP -> {
                    testObject.pr2.setScaleY(testObject.pr2.getScaleY() + factor);
                }
                case KeyCode.DOWN -> {
                    testObject.pr2.setScaleY(testObject.pr2.getScaleY() - factor);
                }
                case KeyCode.PAGE_UP -> {
                    testObject.setScale(
                            testObject.getScaleX() + factor,
                            testObject.getScaleY() + factor
                    );
                }
                case KeyCode.PAGE_DOWN -> {
                    testObject.setScale(
                            testObject.getScaleX() - factor,
                            testObject.getScaleY() - factor
                    );
                }
                case KeyCode.N -> {
                    factorHolder.setValue(factorHolder.getValue() + 0.05f);
                    System.setProperty(PROPERTY_NAME, "" + factorHolder.getValue());
                }
                case KeyCode.M -> {
                    factorHolder.setValue(factorHolder.getValue() - 0.05f);
                    System.setProperty(PROPERTY_NAME, "" + factorHolder.getValue());
                }
            }
        });

        D2D2.loop();
        DebugPanel.saveAll();
    }

    private static class TestObject extends DisplayObjectContainer {

        private final PlainRect pr1;
        private final PlainRect pr2;
        private final PlainRect pr3;

        public TestObject() {
            pr1 = new PlainRect(100, 1, Color.RED);
            pr3 = new PlainRect(100, 1, Color.BLUE);
            pr2 = new PlainRect(100, 1, Color.GREEN);

            pr1.setAlpha(0.75f);
            pr2.setAlpha(0.75f);
            pr3.setAlpha(0.75f);

            add(pr1, 0, 0);
            add(pr2, 0, 1);
            add(pr3, 0, 2);
        }
    }
}
