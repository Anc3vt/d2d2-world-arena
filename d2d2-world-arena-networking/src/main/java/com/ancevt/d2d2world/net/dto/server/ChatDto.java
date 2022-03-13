package com.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.ancevt.d2d2world.net.dto.ChatMessageDto;
import com.ancevt.d2d2world.net.dto.Dto;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDto implements Dto {

    @Builder.Default
    private final List<ChatMessageDto> messages = List.of();
}
