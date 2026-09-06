package ru.otus.hw.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.BookController;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private AuthorService authorService;
    @MockitoBean
    private GenreService genreService;
    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldReturnBookListPage() throws Exception {
        Book book = new Book(1L, "Test Book", new Author(1L, "Author"), List.of());
        when(bookService.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("booklist"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", Matchers.hasSize(1)));
    }

    @Test
    void shouldReturnEditPage() throws Exception {
        long id = 1L;

        Author author = new Author(1L, "Author");
        Genre genre = new Genre(1L, "Genre");
        Book book = new Book(id, "Book", author, List.of(genre));

        when(bookService.findById(id)).thenReturn(Optional.of(book));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(genreService.findAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/edit").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editbook"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("presetGenreId"));
    }


    @Test
    void shouldUpdateBookAndRedirect() throws Exception {
        mockMvc.perform(post("/edit")
                        .param("id", "1")
                        .param("title", "Updated")
                        .param("author.id", "1")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).update(eq(1L), eq("Updated"), eq(1L), anySet());
    }




    @Test
    void shouldReturnCreatePage() throws Exception {
        Author author = new Author(1L, "Author");
        Genre genre = new Genre(1L, "Genre");

        when(authorService.findAll()).thenReturn(List.of(author));
        when(genreService.findAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createbook"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("presetGenreId"));
    }


    @Test
    void shouldCreateBook() throws Exception {
        mockMvc.perform(post("/create")
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(eq("New Book"), eq(1L), anySet());
    }


    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(post("/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(1L);
    }


    @Test
    void shouldReturnBookInfoPage() throws Exception {
        long id = 1L;

        Author author = new Author(1L, "Author");
        Genre genre = new Genre(1L, "Genre");
        Book book = new Book(id, "Book", author, List.of(genre));

        Comment comment = new Comment(1L, "Nice", book);

        when(bookService.findById(id)).thenReturn(Optional.of(book));
        when(commentService.findByBookId(id)).thenReturn(List.of(comment));

        mockMvc.perform(get("/book").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookinfo"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("comments"));
    }
}
