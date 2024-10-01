package org.example.recapbackend.dto;

import lombok.Builder;
import lombok.Data;
import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.model.TodoStatus;

@Data
@Builder
public class ResponseItem {
    private final Long id;
    private final String description;
    private final TodoStatus status;

    public static ResponseItem fromModel(TodoItem item) {
        return ResponseItem.builder()
                .id(item.getId())
                .description(item.getDescription())
                .status(item.getStatus())
                .build();
    }
}
