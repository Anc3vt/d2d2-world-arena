
package com.ancevt.d2d2.event;

import com.ancevt.d2d2.display.texture.TextureAtlas;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TextureUrlLoaderEvent extends Event {

    public static final String TEXTURE_LOAD_COMPLETE = "textureLoadComplete";
    public static final String TEXTURE_LOAD_START = "textureLoadStart";
    public static final String TEXTURE_LOAD_ERROR = "textureLoadError";

    private final byte[] bytes;
    private final TextureAtlas textureAtlas;

}
