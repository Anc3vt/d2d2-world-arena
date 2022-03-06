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
package ru.ancevt.d2d2.lwjgl;

import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.display.IDisplayObjectContainer;
import ru.ancevt.d2d2.display.IFramedDisplayObject;
import ru.ancevt.d2d2.display.IRenderer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.Stage;
import ru.ancevt.d2d2.display.text.BitmapCharInfo;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventPool;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class Renderer implements IRenderer {

    private static final boolean ANTIALIASING_ENABLED = false;
    private final Stage stage;
    private long windowId;
    private LWJGLTextureEngine textureEngine;

    public Renderer(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init(long windowId) {
        this.windowId = windowId;

        GL30.glEnable(GL_BLEND);
        GL30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void reshape(int width, int height) {
        GL30.glViewport(0, 0, width, height);
        GL30.glMatrixMode(GL30.GL_PROJECTION);
        GL30.glLoadIdentity();
        GLU.gluOrtho2D(0, width, height, 0);
        GL30.glMatrixMode(GL30.GL_MODELVIEW);
        GL30.glLoadIdentity();
    }

    @Override
    public void renderFrame() {
        Root root = stage.getRoot();
        if (root == null) return;

        textureEngine.loadTextureAtlases();

        clear();

        //GL30.glEnable( GL30.GL_LINE_SMOOTH );
        //GL30.glEnable( GL30.GL_POLYGON_SMOOTH );
        //GL30.glHint( GL30.GL_LINE_SMOOTH_HINT, GL30.GL_NICEST );
        //GL30.glHint( GL30.GL_POLYGON_SMOOTH_HINT, GL30.GL_NICEST );
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glMatrixMode(GL30.GL_MODELVIEW);
        GL30.glLoadIdentity();

        renderDisplayObject(stage,
                0,
                stage.getX(),
                stage.getY(),
                stage.getScaleX(),
                stage.getScaleY(),
                stage.getRotation(),
                stage.getAlpha()
        );

        textureEngine.unloadTextureAtlases();

    }

    private void clear() {
        Color backgroundColor = stage.getRoot().getBackgroundColor();
        float backgroundColorRed = backgroundColor.getR() / 255.0f;
        float backgroundColorGreen = backgroundColor.getG() / 255.0f;
        float backgroundColorBlue = backgroundColor.getB() / 255.0f;
        GL30.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, 1.0f);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
    }

    float _a = 0;

    private void renderDisplayObject(IDisplayObject displayObject,
                                     int level,
                                     float toX,
                                     float toY,
                                     float toScaleX,
                                     float toScaleY,
                                     float toRotation,
                                     float toAlpha) {

        if (!displayObject.isVisible()) return;

        float scX = displayObject.getScaleX() * toScaleX;
        float scY = displayObject.getScaleY() * toScaleY;
        float r = displayObject.getRotation() + toRotation;

        float x = toScaleX * displayObject.getX();
        float y = toScaleY * displayObject.getY();

        float a = displayObject.getAlpha() * toAlpha;


        GL30.glPushMatrix();
        GL30.glTranslatef(x, y, 0);
        GL30.glRotatef(r, 0, 0, 1);

        if (displayObject instanceof IDisplayObjectContainer) {
            IDisplayObjectContainer container = (DisplayObjectContainer) displayObject;

            for (int i = 0; i < container.getChildCount(); i++) {

                final IDisplayObject currentChild = container.getChild(i);

                renderDisplayObject(
                        currentChild,
                        level + 1,
                        x + toX,
                        y + toY,
                        scX,
                        scY,
                        0,
                        a
                );
            }

        } else if (displayObject instanceof Sprite s) {
            renderSprite(s, a, scX, scY);
        } else if (displayObject instanceof BitmapText btx) {
            renderBitmapText(btx, a, scX, scY);
        }

        if (displayObject instanceof IFramedDisplayObject f) {
            f.processFrame();
        }

        displayObject.onEachFrame();
        displayObject.dispatchEvent(EventPool.simpleEventSingleton(Event.EACH_FRAME, displayObject));

        GL30.glPopMatrix();
    }

    private void renderSprite(Sprite sprite, float alpha, float scaleX, float scaleY) {
        Texture texture = sprite.getTexture();

        if (texture == null) return;
        if (texture.getTextureAtlas().isDisposed()) return;

        TextureAtlas textureAtlas = texture.getTextureAtlas();

        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        boolean bindResult = D2D2.getTextureManager().getTextureEngine().bind(textureAtlas);

        if (!bindResult) {
            return;
        }

        D2D2.getTextureManager().getTextureEngine().enable(textureAtlas);

        textureParamsHandle();

        final Color color = sprite.getColor();

        if (color != null) {
            GL30.glColor4f(
                    color.getR() / 255f,
                    color.getG() / 255f,
                    color.getB() / 255f,
                    alpha
            );
        }

        int tX = texture.x();
        int tY = texture.y();
        int tW = texture.width();
        int tH = texture.height();

        float totalW = textureAtlas.getWidth();
        float totalH = textureAtlas.getHeight();

        float x = tX / totalW;
        float y = tY / totalH;
        float w = tW / totalW;
        float h = tH / totalH;

        float repeatX = sprite.getRepeatX();
        float repeatY = sprite.getRepeatY();

        for (int rY = 0; rY < repeatY; rY++) {
            for (int rX = 0; rX < repeatX; rX++) {

                float px = rX * tW * scaleX;
                float py = rY * tH * scaleY;

                GL30.glBegin(GL30.GL_QUADS);
                GL30.glTexCoord2d(x, h + y);
                GL30.glVertex2d(px + 0, py + tH * scaleY);
                GL30.glTexCoord2d(w + x, h + y);
                GL30.glVertex2d(px + tW * scaleX, py + tH * scaleY);
                GL30.glTexCoord2d(w + x, y);
                GL30.glVertex2d(px + tW * scaleX, py + 0);
                GL30.glTexCoord2d(x, y);
                GL30.glVertex2d(px + 0, py + 0);
                GL30.glEnd();
            }
        }

        GL30.glDisable(GL30.GL_BLEND);
        D2D2.getTextureManager().getTextureEngine().disable(textureAtlas);
    }

    private void renderBitmapText(BitmapText bitmapText, float alpha, float scaleX, float scaleY) {
        if (bitmapText.isEmpty()) return;

        BitmapFont bitmapFont = bitmapText.getBitmapFont();
        TextureAtlas textureAtlas = bitmapFont.getTextureAtlas();

        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        boolean bindResult = D2D2.getTextureManager().getTextureEngine().bind(textureAtlas);

        if (!bindResult) return;

        D2D2.getTextureManager().getTextureEngine().enable(textureAtlas);

        textureParamsHandle();

        Color color = bitmapText.getColor();

        GL30.glColor4f(
                (float) color.getR() / 255f,
                (float) color.getG() / 255f,
                (float) color.getB() / 255f,
                alpha
        );

        String text = bitmapText.getText();

        int textureWidth = textureAtlas.getWidth();
        int textureHeight = textureAtlas.getHeight();

        float lineSpacing = bitmapText.getLineSpacing();
        float spacing = bitmapText.getSpacing();

        float boundWidth = bitmapText.getBoundWidth() * bitmapText.getAbsoluteScaleX();
        float boundHeight = bitmapText.getBoundHeight() * bitmapText.getAbsoluteScaleY();

        float drawX = 0;
        float drawY = 0;

        GL30.glBegin(GL30.GL_QUADS);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            BitmapCharInfo charInfo = bitmapFont.getCharInfo(c);

            if (charInfo == null) {
                continue;
            }

            float charWidth = charInfo.width();
            float charHeight = charInfo.height();

            if (c == '\n' || (boundWidth != 0 && drawX >= boundWidth - charWidth)) {
                drawX = 0;
                drawY += (charHeight + lineSpacing) * scaleY;

                if (boundHeight != 0 && drawY > boundHeight) {
                    break;
                }
            }

            drawChar(drawX, (drawY + scaleY * charHeight), textureWidth, textureHeight, charInfo, scaleX, scaleY);

            drawX += (charWidth + (c != '\n' ? spacing : 0)) * scaleX;
        }

        GL30.glEnd();

        GL30.glDisable(GL30.GL_BLEND);
        D2D2.getTextureManager().getTextureEngine().disable(textureAtlas);
    }

    float nextHalf(float v) {
        return (float) (Math.ceil(v * 2) / 2);
    }

    private void drawChar(
            float x,
            float y,
            int textureAtlasWidth,
            int textureAtlasHeight,
            BitmapCharInfo charInfo,
            float scX,
            float scY) {

        //scX = nextHalf(scX);
        scY = nextHalf(scY);

        float charWidth = charInfo.width();
        float charHeight = charInfo.height();

        float xOnTexture = charInfo.x();
        float yOnTexture = charInfo.y() + charHeight;

        float cx = xOnTexture / textureAtlasWidth;
        float cy = -yOnTexture / textureAtlasHeight;
        float cw = charWidth / textureAtlasWidth;
        float ch = -charHeight / textureAtlasHeight;

        GL30.glTexCoord2d(cx, -cy);
        GL30.glVertex2d(x, y);
        GL30.glTexCoord2d(cx + cw, -cy);
        GL30.glVertex2d(charWidth * scX + x, y);
        GL30.glTexCoord2d(cx + cw, -cy + ch);
        GL30.glVertex2d(charWidth * scX + x, charHeight * -scY + y);
        GL30.glTexCoord2d(cx, -cy + ch);
        GL30.glVertex2d(x, charHeight * -scY + y);
    }

    private static void textureParamsHandle() {
        if (ANTIALIASING_ENABLED) {
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        } else {
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        }

    }

    public void setLWJGLTextureEngine(LWJGLTextureEngine textureEngine) {
        this.textureEngine = textureEngine;
    }

}





























