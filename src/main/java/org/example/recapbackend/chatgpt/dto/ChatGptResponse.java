package org.example.recapbackend.chatgpt.dto;

import java.util.List;

public record ChatGptResponse(List<ChatGptResponseChoice> choices) {
}
