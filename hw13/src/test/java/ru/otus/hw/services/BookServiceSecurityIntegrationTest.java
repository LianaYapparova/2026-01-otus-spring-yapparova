package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaDataBookRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookServiceSecurityIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private JpaDataBookRepository bookRepository;

    @Test
    @WithMockUser(username = "administrator", roles = "ADMIN")
    void shouldAllowAdministratorToReadBooks() {
        var books = bookService.findAll();

        assertThat(books).hasSize(3);
        assertThat(bookService.findById(1L)).isPresent();
    }

    @Test
    @WithMockUser(username = "administrator", roles = "ADMIN")
    void shouldAllowAdministratorToSaveBook() {
        var savedBook = bookService.insert("Admin book", 1L, Set.of(1L, 2L));

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Admin book");
        assertThat(bookRepository.findById(savedBook.getId())).isPresent();
    }

    @Test
    @WithMockUser(username = "administrator", roles = "ADMIN")
    void shouldAllowAdministratorToUpdateBook() {
        var updatedBook = bookService.update(1L, "Admin updated book", 2L, Set.of(3L));

        assertThat(updatedBook.getId()).isEqualTo(1L);
        assertThat(updatedBook.getTitle()).isEqualTo("Admin updated book");
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2L);
        assertThat(updatedBook.getGenres()).extracting("id").containsExactly(3L);
    }

    @Test
    @WithMockUser(username = "administrator", roles = "ADMIN")
    void shouldAllowAdministratorToDeleteBook() {
        assertThatCode(() -> bookService.deleteById(1L)).doesNotThrowAnyException();

        assertThat(bookRepository.findById(1L)).isEmpty();
    }

    @Test
    @WithMockUser(username = "ivanov", roles = "USER")
    void shouldAllowIvanovToReadOnlyOwnBooks() {
        var books = bookService.findAll();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getId()).isEqualTo(3L);
        assertThat(bookService.findById(3L)).isPresent();
        assertThatThrownBy(() -> bookService.findById(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "ivanov", roles = "USER")
    void shouldDenyIvanovWriteOperations() {
        assertThatThrownBy(() -> bookService.insert("Ivanov book", 1L, Set.of(1L)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> bookService.update(3L, "Ivanov updated book", 1L, Set.of(1L)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> bookService.deleteById(3L))
                .isInstanceOf(AccessDeniedException.class);
    }
}
