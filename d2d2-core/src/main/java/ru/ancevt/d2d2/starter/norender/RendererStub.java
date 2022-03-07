/*
 *   D2D2 core
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
package ru.ancevt.d2d2.starter.norender;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2.display.*;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventPool;

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

        if (displayObject instanceof IDisplayObjectContainer container) {
            for (int i = 0; i < container.getChildCount(); i++) {
                renderDisplayObject(container.getChild(i));
            }
        }

        if (displayObject instanceof IFramedDisplayObject f) {
            f.processFrame();
        }

        displayObject.onEachFrame();
        displayObject.dispatchEvent(EventPool.simpleEventSingleton(Event.EACH_FRAME, displayObject));
    }
}
