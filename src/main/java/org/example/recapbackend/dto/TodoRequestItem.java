package org.example.recapbackend.dto;

import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.model.TodoStatus;

public record TodoRequestItem(
        String description,
        TodoStatus status
) {
    public TodoItem toModel() {
        return TodoItem.builder()
                .description(description)
                .status(status)
                .build();
    }
}
