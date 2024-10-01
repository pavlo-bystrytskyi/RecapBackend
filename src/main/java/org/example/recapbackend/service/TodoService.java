package org.example.recapbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.recapbackend.chatgpt.service.ChatGptService;
import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.model.TodoRepository;
import org.example.recapbackend.model.TodoStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    public static final String NOT_FOUND_MESSAGE = "TODO element with id %s not found.";

    private final TodoRepository repository;

    private final ChatGptService chatGptService;

    public void initialize(String topic) {
        repository.deleteAll();
        List<String> generatedTodo = chatGptService.generate(topic);
        generatedTodo.stream()
                .map(
                        (description) -> TodoItem.builder().status(TodoStatus.OPEN).description(description).build()
                )
                .forEach(repository::save);
    }

    public List<TodoItem> getAll() {
        return repository.findAll();
    }

    public TodoItem get(Long id) {
        Optional<TodoItem> savedItem = repository.findById(id);
        if (savedItem.isEmpty()) {
            throw new NoSuchElementException(NOT_FOUND_MESSAGE.formatted(id));
        }

        return savedItem.get();
    }

    public TodoItem create(TodoItem item) {
        String correctedDescription = chatGptService.correctText(item.getDescription());

        return repository.save(item.withDescription(correctedDescription));
    }

    public TodoItem update(Long id, TodoItem item) {
        Optional<TodoItem> savedItem = repository.findById(id);
        if (savedItem.isEmpty()) {
            throw new NoSuchElementException(NOT_FOUND_MESSAGE.formatted(id));
        }

        return repository.save(item.withId(id));
    }

    public void delete(Long id) {
        Optional<TodoItem> itemToDelete = repository.findById(id);
        if (itemToDelete.isEmpty()) {
            throw new NoSuchElementException(NOT_FOUND_MESSAGE.formatted(id));
        }

        repository.deleteById(id);
    }
}
