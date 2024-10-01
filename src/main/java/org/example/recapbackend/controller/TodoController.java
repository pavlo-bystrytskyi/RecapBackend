package org.example.recapbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.recapbackend.dto.RequestItem;
import org.example.recapbackend.dto.ResponseItem;
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
    public List<ResponseItem> getAllTodos() {
        return service.getAll().stream().map(ResponseItem::fromModel).toList();
    }

    @GetMapping("{id}")
    public ResponseItem getTodoById(@PathVariable Long id) {
        TodoItem item = service.get(id);

        return ResponseItem.fromModel(item);
    }

    @PostMapping
    public ResponseItem postTodo(@RequestBody RequestItem data) {
        TodoItem item = service.create(data.toModel());

        return ResponseItem.fromModel(item);
    }

    @PutMapping("{id}")
    public ResponseItem putTodo(@PathVariable Long id, @RequestBody RequestItem data) {
        TodoItem item = service.update(id, data.toModel());

        return ResponseItem.fromModel(item);
    }

    @DeleteMapping("{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.delete(id);
    }
}
