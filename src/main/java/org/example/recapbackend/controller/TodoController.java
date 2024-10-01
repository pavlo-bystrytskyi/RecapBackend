package org.example.recapbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.recapbackend.dto.TodoRequestItem;
import org.example.recapbackend.dto.TodoResponseItem;
import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    @GetMapping
    public List<TodoResponseItem> getAllTodos() {
        return service.getAll().stream().map(TodoResponseItem::fromModel).toList();
    }

    @GetMapping("/{id}")
    public TodoResponseItem getTodoById(@PathVariable Long id) {
        TodoItem item = service.get(id);

        return TodoResponseItem.fromModel(item);
    }

    @PostMapping
    public TodoResponseItem postTodo(@RequestBody TodoRequestItem data) {
        TodoItem item = service.create(data.toModel());

        return TodoResponseItem.fromModel(item);
    }

    @PutMapping("/{id}")
    public TodoResponseItem putTodo(@PathVariable Long id, @RequestBody TodoRequestItem data) {
        TodoItem item = service.update(id, data.toModel());

        return TodoResponseItem.fromModel(item);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/initialize/{topic}")
    public List<TodoResponseItem> initializeTopic(@PathVariable String topic) {
        service.initialize(topic);

        return getAllTodos();
    }
}
