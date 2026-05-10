package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.NotFoundException;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.rest.BookRestController;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldReturnBooksList() throws Exception {
        List<Book> books = List.of(
                new Book(1L, "Book1", new Author(), List.of((new Genre()))),
                new Book(2L, "Book2", new Author(), List.of((new Genre()))
                ));

        given(bookService.findAll()).willReturn(books);

        List<BookDto> expected = books.stream()
                .map(BookDto::fromDomainObject)
                .toList();

        mvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        Book book = new Book(1L, "Book1", new Author(), List.of((new Genre())));

        given(bookService.findById(1L)).willReturn(Optional.of(book));

        BookDto expected = BookDto.fromDomainObject(book);

        mvc.perform(get("/api/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }


    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/book/1"))
                .andExpect(status().isOk());

        verify(bookService).deleteById(1L);
    }

    @Test
    void shouldReturnComments() throws Exception {
        List<Comment> comments = List.of(
                new Comment(1L, "c1", new Book()),
                new Comment(2L, "c2", new Book())
        );

        given(commentService.findByBookId(1L)).willReturn(comments);

        List<CommentDto> expected = comments.stream()
                .map(CommentDto::fromDomainObject)
                .toList();

        mvc.perform(get("/api/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Map<String, Object> request = Map.of(
                "title", "New Title",
                "authorId", 1,
                "genreIds", List.of(1L, 2L)
        );

        mvc.perform(put("/api/book/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService).update(
                eq(1L),
                eq("New Title"),
                eq(1L),
                eq(Set.of(1L, 2L))
        );
    }

    @Test
    void shouldSaveBook() throws Exception {
        Map<String, Object> request = Map.of(
                "title", "Book",
                "authorId", 1,
                "genreIds", List.of(1L, 2L)
        );

        mvc.perform(post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService).insert(
                eq("Book"),
                eq(1l),
                anySet()
        );
    }
}