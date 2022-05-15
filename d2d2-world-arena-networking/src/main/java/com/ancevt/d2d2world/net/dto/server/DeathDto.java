
package com.ancevt.d2d2world.net.dto.server;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class DeathDto implements Dto {
    private final Integer deadPlayerId;
    private final Integer killerPlayerId;
}
