
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class PackedScenery extends Sprite {
	public PackedScenery(final @NotNull TextureAtlas textureAtlas) {
		super(textureAtlas.createTexture());
		setTextureBleedingFix(0f);
	}
}
