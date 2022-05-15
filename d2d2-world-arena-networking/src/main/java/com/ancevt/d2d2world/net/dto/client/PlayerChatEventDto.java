
package com.ancevt.d2d2world.net.dto.client;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class PlayerChatEventDto implements Dto {

    public static final String OPEN = "open";
    public static final String CLOSE = "close";

    private final String action;
    private final int playerId;
}
