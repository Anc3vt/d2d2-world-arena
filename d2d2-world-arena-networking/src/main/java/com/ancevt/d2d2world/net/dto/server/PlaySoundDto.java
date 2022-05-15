
package com.ancevt.d2d2world.net.dto.server;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaySoundDto implements Dto {
    private final String mapkitName;
    private final String file;
    private final Float x;
    private final Float y;
}
