
package com.ancevt.d2d2world.net.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageDto implements Dto {

    private final int id;
    private final String text;
    private final int textColor;
    @Builder.Default
    private final PlayerDto player = null;
}
