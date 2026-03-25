package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
@Import({BookServiceImpl.class, JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class})
public class BookServiceIntegrationTest {

    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private JpaBookRepository jpaBookRepository;
    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;
    @Autowired
    private JpaGenreRepository genreRepository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;


    @BeforeEach
    void setUp() {
        dbAuthors = jpaAuthorRepository.findAll();
        dbGenres = genreRepository.findAll();
        dbBooks = jpaBookRepository.findAll();
    }

    @Test
    @DisplayName("Должен загружать книгу со всеми связями без LazyInitializationException")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldLoadBookWithRelationsWithoutLazyExceptionTest() {
        var book = bookService.findById(dbBooks.get(0).getId()).orElseThrow();

        assertThatCode(() -> {
            book.getAuthor().getFullName();
            book.getGenres().size();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен загружать все книги со всеми связями без LazyInitializationException")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldLoadAllBooksWithoutLazyExceptionTest() {
        var books = bookService.findAll();
        assertThat(books).hasSize(jpaAuthorRepository.findAll().size());
        assertThatCode(() -> {
            books.forEach(book -> {
                assertThat(book.getAuthor()).isNotNull();
                assertThat(book.getAuthor().getFullName()).isNotBlank();
                assertThat(book.getGenres()).isNotEmpty();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен вставлять новую книгу с корректными связями")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldInsertBookWithRelationsTest() {
        Set<Long> genreIds = Set.of(dbGenres.get(0).getId(), dbGenres.get(1).getId());
        Book savedBook = bookService.insert("New book", dbAuthors.get(0).getId(), genreIds);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("New book");
        assertThat(savedBook.getAuthor().getId()).isEqualTo(dbAuthors.get(0).getId());
        assertThat(savedBook.getAuthor().getFullName()).isEqualTo(dbAuthors.get(0).getFullName());
        assertThat(savedBook.getGenres()).hasSize(2);

        Book foundBook = jpaBookRepository.findById(savedBook.getId()).orElseThrow();
        assertThat(foundBook.getTitle()).isEqualTo("New book");
    }

    @Test
    @DisplayName("Обновление существующей книги")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldUpdateBookTest() {
        String newTitle = "new title";
        Set<Long> genreIds = Set.of(dbGenres.get(2).getId());
        Book updatedBook = bookService.update(dbBooks.get(0).getId(), newTitle, dbAuthors.get(1).getId(), genreIds);

        assertThat(updatedBook.getId()).isEqualTo(dbBooks.get(0).getId());
        assertThat(updatedBook.getTitle()).isEqualTo(newTitle);
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(dbAuthors.get(1).getId());
        assertThat(updatedBook.getGenres()).hasSize(1);


        Book foundBook = jpaBookRepository.findById(dbBooks.get(0).getId()).orElseThrow();
        assertThat(foundBook.getTitle()).isEqualTo(newTitle);
        assertThat(foundBook.getAuthor().getId()).isEqualTo(dbAuthors.get(1).getId());
    }

    @Test
    @DisplayName("Удаление книги по ID")
    void shouldDeleteBookById() {
        bookService.deleteById(dbBooks.get(0).getId());

        assertThat(jpaBookRepository.findById(dbBooks.get(0).getId()).isEmpty()).isTrue();

        assertThat(jpaAuthorRepository.findById(dbBooks.get(0).getAuthor().getId())).isPresent();
        assertThat(genreRepository.findAllByIds(dbBooks.get(0).getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))).isNotEmpty();
    }
}
