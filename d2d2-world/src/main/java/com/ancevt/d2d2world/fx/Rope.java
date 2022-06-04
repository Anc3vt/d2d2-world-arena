/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.fx;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.D2D2WorldAssets;

public class Rope extends Sprite {

    private float degree;
    private float length;

    public Rope(Texture texture) {
        setTexture(texture);
    }

    public void setDegree(float degree) {
        this.degree = degree;
        setRotation(degree);
    }

    public float getDegree() {
        return degree;
    }

    public void setLength(float length) {
        this.length = length;
        int repeat = (int) (length / getTexture().width());
        setRepeatX(repeat);
        System.out.println(getRepeatX());
    }

    public float getLength() {
        return length;
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2World.init(false, false);
        Rope rope = new Rope(D2D2WorldAssets.getRopeTexture());
        rope.setLength(100);
        rope.setDegree(60);
        stage.add(rope, 100, 100);
        D2D2.loop();
    }
}
