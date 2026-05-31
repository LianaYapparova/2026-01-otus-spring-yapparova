package ru.otus.hw.secutity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.BookController;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldRedirectToLoginWhenUserNotAuthenticated() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldReturnBookListPageForAuthenticatedUser() throws Exception {

        mockMvc.perform(get("/")
                        .with(user("petrov")
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCreateBookPageForAuthenticatedUser() throws Exception {
        Genre genre = new Genre(1L, "Fantasy");

        when(genreService.findAll())
                .thenReturn(List.of(genre));

        mockMvc.perform(get("/create")
                        .with(user("petrov")
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateBook() throws Exception {

        mockMvc.perform(post("/create")
                        .with(user("petrov")
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(
                "New Book",
                1L,
                java.util.Set.of(1L, 2L)
        );
    }

    @Test
    void shouldDeleteBook() throws Exception {

        mockMvc.perform(post("/delete")
                        .with(user("petrov")
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(1L);
    }
}
