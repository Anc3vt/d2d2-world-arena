
package com.ancevt.d2d2.backend.norender;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventPool;

public class RendererStub implements IRenderer {

    private final Stage stage;

    public RendererStub(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init(long windowId) {

    }

    @Override
    public void reshape(int width, int height) {

    }

    @Override
    public void renderFrame() {
        Root root = stage.getRoot();
        if (root == null) return;

        renderDisplayObject(stage);
    }

    private void renderDisplayObject(@NotNull IDisplayObject displayObject) {
        if (!displayObject.isVisible()) return;

        if (displayObject instanceof IDisplayObjectContainer) {
            IDisplayObjectContainer container = (IDisplayObjectContainer) displayObject;
            for (int i = 0; i < container.getChildCount(); i++) {
                renderDisplayObject(container.getChild(i));
            }
        }

        if (displayObject instanceof IFramedDisplayObject) {
            ((IFramedDisplayObject) displayObject).processFrame();
        }

        displayObject.onEachFrame();
        displayObject.dispatchEvent(EventPool.simpleEventSingleton(Event.EACH_FRAME, displayObject));
    }
}
