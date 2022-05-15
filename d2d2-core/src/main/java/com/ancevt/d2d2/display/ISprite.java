
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.display.texture.Texture;

public interface ISprite extends IDisplayObject, IColored, IRepeatable {
    Texture getTexture();

    void setTexture(Texture value);

    void setTexture(String textureKey);

    ISprite cloneSprite();

    void setTextureBleedingFix(double v);

    double getTextureBleedingFix();

    void setVertexBleedingFix(double v);

    double getVertexBleedingFix();

    ShaderProgram getShaderProgram();

    void setShaderProgram(ShaderProgram shaderProgram);
}
