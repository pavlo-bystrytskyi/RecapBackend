package org.example.recapbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.recapbackend.chatgpt.dto.ChatGptResponse;
import org.example.recapbackend.chatgpt.dto.ChatGptResponseChoice;
import org.example.recapbackend.chatgpt.dto.ChatGptResponseChoiceMessage;
import org.example.recapbackend.dto.RequestItem;
import org.example.recapbackend.model.TodoItem;
import org.example.recapbackend.model.TodoRepository;
import org.example.recapbackend.model.TodoStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class TodoControllerTest {

    private static final Long ID_FIRST = 1L;
    private static final Long ID_SECOND = 2L;
    private static final Long ID_THIRD = 3L;
    private static final String DESCRIPTION_FIRST = "First todo";
    private static final String DESCRIPTION_SECOND = "Second todo";
    private static final String DESCRIPTION_CORRECTED = "Corrected todo";
    private static final TodoStatus STATUS_FIRST = TodoStatus.DONE;
    private static final TodoStatus STATUS_SECOND = TodoStatus.OPEN;
    private static final String PATH_BASIC = "/api/todo";
    private static final String PATH_PARAMETRIZED = "/api/todo/{id}";
    private static final String MESSAGE_ERROR = "TODO element with id %s not found.";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private TodoRepository repository;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeData() throws Exception {
        repository.save(TodoItem.builder().description(DESCRIPTION_FIRST).status(STATUS_FIRST).build());
        repository.save(TodoItem.builder().description(DESCRIPTION_SECOND).status(STATUS_SECOND).build());
    }

    private void setUpChatGptMock(String response) throws Exception {
        ChatGptResponse chatGptResponse = new ChatGptResponse(
                List.of(
                        new ChatGptResponseChoice(
                                new ChatGptResponseChoiceMessage(
                                        "User",
                                        response
                                )
                        )
                )
        );
        mockRestServiceServer.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withSuccess(
                                asJsonString(chatGptResponse),
                                MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @DirtiesContext
    void getAllTodosTest_notEmpty() throws Exception {
        initializeData();
        mvc.perform(get(PATH_BASIC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID_FIRST))
                .andExpect(jsonPath("$[0].description").value(DESCRIPTION_FIRST))
                .andExpect(jsonPath("$[0].status").value(STATUS_FIRST.toString()))
                .andExpect(jsonPath("$[1].id").value(ID_SECOND))
                .andExpect(jsonPath("$[1].description").value(DESCRIPTION_SECOND))
                .andExpect(jsonPath("$[1].status").value(STATUS_SECOND.toString()));
    }

    @Test
    void getAllTodosTest_empty() throws Exception {
        mvc.perform(get(PATH_BASIC))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    @DirtiesContext
    void getTodoByIdTest_found() throws Exception {
        initializeData();
        mvc.perform(get(PATH_PARAMETRIZED, ID_SECOND))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ID_SECOND))
                .andExpect(jsonPath("description").value(DESCRIPTION_SECOND))
                .andExpect(jsonPath("status").value(STATUS_SECOND.toString()));
    }

    @Test
    @DirtiesContext
    void getTodoByIdTest_notFound() throws Exception {
        initializeData();
        mvc.perform(get(PATH_PARAMETRIZED, ID_THIRD))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error").value(MESSAGE_ERROR.formatted(ID_THIRD)));
    }

    @Test
    @DirtiesContext
    void postTodoTest() throws Exception {
        setUpChatGptMock(DESCRIPTION_CORRECTED);
        mvc.perform(
                        post(PATH_BASIC)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new RequestItem(DESCRIPTION_SECOND, STATUS_SECOND))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ID_FIRST))
                .andExpect(jsonPath("description").value(DESCRIPTION_CORRECTED))
                .andExpect(jsonPath("status").value(STATUS_SECOND.toString())
                );

        Optional<TodoItem> todoItemOptional = repository.findById(ID_FIRST);
        assertTrue(todoItemOptional.isPresent());
        TodoItem todoItem = todoItemOptional.get();
        assertEquals(ID_FIRST, todoItem.getId());
        assertEquals(DESCRIPTION_CORRECTED, todoItem.getDescription());
        assertEquals(STATUS_SECOND, todoItem.getStatus());

    }

    @Test
    @DirtiesContext
    void putTodoTest_found() throws Exception {
        initializeData();
        mvc.perform(
                        put(PATH_PARAMETRIZED, ID_FIRST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new RequestItem(DESCRIPTION_SECOND, STATUS_SECOND))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ID_FIRST))
                .andExpect(jsonPath("description").value(DESCRIPTION_SECOND))
                .andExpect(jsonPath("status").value(STATUS_SECOND.toString())
                );

        Optional<TodoItem> todoItemOptional = repository.findById(ID_FIRST);
        assertTrue(todoItemOptional.isPresent());
        TodoItem todoItem = todoItemOptional.get();
        assertEquals(ID_FIRST, todoItem.getId());
        assertEquals(DESCRIPTION_SECOND, todoItem.getDescription());
        assertEquals(STATUS_SECOND, todoItem.getStatus());
    }

    @Test
    @DirtiesContext
    void putTodoTest_notFound() throws Exception {
        initializeData();
        mvc.perform(
                        put(PATH_PARAMETRIZED, ID_THIRD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new RequestItem(DESCRIPTION_SECOND, STATUS_SECOND))))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error").value(MESSAGE_ERROR.formatted(ID_THIRD)));

        Optional<TodoItem> todoItemOptional = repository.findById(ID_THIRD);
        assertTrue(todoItemOptional.isEmpty());
    }

    @Test
    @DirtiesContext
    void deleteTodoTest_found() throws Exception {
        initializeData();
        mvc.perform(delete(PATH_PARAMETRIZED, ID_FIRST))
                .andExpect(status().isOk());

        Optional<TodoItem> todoItemOptional = repository.findById(ID_FIRST);
        assertTrue(todoItemOptional.isEmpty());
    }

    @Test
    @DirtiesContext
    void deleteTodoTest_notFound() throws Exception {
        initializeData();
        mvc.perform(get(PATH_PARAMETRIZED, ID_THIRD))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error").value(MESSAGE_ERROR.formatted(ID_THIRD)));
    }
}