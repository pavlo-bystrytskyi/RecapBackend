package org.example.recapbackend.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatGptRequest(
        String model,
        List<ChatGptRequestMessage> messages
) {
}
