package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.rest.AuthorRestController;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldReturnCorrectAuthorsList() throws Exception {
        List<Author> authors = List.of(
                new Author(1, "Author1"),
                new Author(2, "Author2")
        );

        given(authorService.findAll()).willReturn(authors);

        List<AuthorDto> expectedResult = authors.stream()
                .map(AuthorDto::fromDomainObject)
                .toList();

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    void shouldReturnEmptyListWhenNoAuthors() throws Exception {
        given(authorService.findAll()).willReturn(List.of());

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}