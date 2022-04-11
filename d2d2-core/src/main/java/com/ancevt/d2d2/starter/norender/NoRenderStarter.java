/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2.starter.norender;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.D2D2Starter;
import com.ancevt.d2d2.display.IRenderer;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.starter.lwjgl.LWJGLTextureEngine;

import static java.lang.Thread.sleep;

public class NoRenderStarter implements D2D2Starter {

    private int width;
    private int height;
    private Stage stage;
    private String title;
    private IRenderer renderer;
    private boolean alive;

    public NoRenderStarter(int width, int height) {
        D2D2.getTextureManager().setTextureEngine(new LWJGLTextureEngine());
        setSize(width, height);
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.setStageSize(width, height);
        stage.onResize(width, height);
        renderer = new RendererStub(stage);
    }

    @Override
    public void start() {
        alive = true;
        startNoRenderLoop();
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void stop() {
        alive = false;
    }

    @Override
    public void setMouseVisible(boolean mouseVisible) {

    }

    @Override
    public boolean isMouseVisible() {
        return false;
    }

    @Override
    public void putToClipboard(String string) {

    }

    @Override
    public String getStringFromClipboard() {
        return null;
    }

    private void startNoRenderLoop() {
        while (alive) {
            try {
                sleep(1000 / 70);//1000/60);
                renderer.renderFrame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isFullscreen() {
        return false;
    }

    @Override
    public void setFullscreen(boolean value) {

    }

    @Override
    public void setWindowXY(int x, int y) {

    }

    @Override
    public int getWindowX() {
        return 0;
    }

    @Override
    public int getWindowY() {
        return 0;
    }
}
