package org.example.recapbackend.chatgpt.service;

import org.example.recapbackend.chatgpt.dto.ChatGptRequest;
import org.example.recapbackend.chatgpt.dto.ChatGptRequestMessage;
import org.example.recapbackend.chatgpt.dto.ChatGptResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatGptService {
    private static final String PROMPT_TEMPLATE = """
            Perform a spelling and grammar correction of the following text.
            Provide only the corrected version of the text in your response, without any additional comments or explanations.
            Here is the text to correct:
            %s
            """;

    private final RestClient restClient;

    public ChatGptService(
            RestClient.Builder restClientBuilder,
            @Value("${chat.gpt.api.token}") String chatGptApiToken) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + chatGptApiToken)
                .build();
    }

    public String correctText(String text) {
        String prompt = PROMPT_TEMPLATE.formatted(text);

        return complete(prompt).choices().getFirst().message().content();
    }

    private ChatGptResponse complete(
            String prompt
    ) {
        ChatGptRequestMessage message = new ChatGptRequestMessage("user", prompt);
        ChatGptRequest request = new ChatGptRequest("gpt-4o-mini", List.of(message));

        return restClient
                .post()
                .body(request)
                .retrieve()
                .body(ChatGptResponse.class);
    }
}
