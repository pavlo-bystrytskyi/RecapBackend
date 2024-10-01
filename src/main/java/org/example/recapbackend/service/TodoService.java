package org.example.recapbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.model.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    public static final String NOT_FOUND_MESSAGE = "TODO element with id %s not found.";

    private final TodoRepository repository;

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
        return repository.save(item);
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
