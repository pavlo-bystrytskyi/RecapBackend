package org.example.recapbackend.chatgpt.dto;

import java.util.List;

public record ChatGptRequest(String model, List<ChatGptRequestMessage> messages) {
}
