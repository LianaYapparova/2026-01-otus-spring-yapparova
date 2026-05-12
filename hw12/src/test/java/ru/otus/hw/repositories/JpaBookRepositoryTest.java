package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами")
@DataJpaTest
public class JpaBookRepositoryTest {
    @Autowired
    private JpaDataBookRepository repositoryJpa;
    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("Должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        var book = repositoryJpa.findById(1L);

        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("BookTitle_1");
        assertThat(book.get().getAuthor().getFullName()).isEqualTo("Author_1");
        assertThat(book.get().getGenres()).hasSize(2);
    }

    @Test
    @DisplayName("Должен загружать список всех книг")
    void shouldReturnCorrectBooksList() {
        var actualBooks = repositoryJpa.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedBooks);
    }

    @Test
    @DisplayName("Должен сохранять новую книгу")
    void shouldSaveNewBook() {
        var expectedBook = new Book(
                0,
                "BookTitle_10500",
                em.find(Author.class, 1L),
                List.of(
                        em.find(Genre.class, 1L),
                        em.find(Genre.class, 3L)
                )
        );

        var returnedBook = repositoryJpa.save(expectedBook);

        var actualBook = em.find(Book.class, returnedBook.getId());

        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Должен сохранять измененную книгу")
    void shouldSaveUpdatedBook() {
        var book = em.find(Book.class, dbBooks.get(0).getId());

        book.setTitle("BookTitle_10500");
        book.setAuthor(em.find(Author.class, 3L));
        book.setGenres(new ArrayList<>(List.of(
                em.find(Genre.class, 5L),
                em.find(Genre.class, 6L)
        )));

        repositoryJpa.save(book);

        var updatedBook = em.find(Book.class, dbBooks.get(0).getId());

        assertThat(updatedBook.getTitle()).isEqualTo("BookTitle_10500");
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(3L);
        assertThat(updatedBook.getGenres())
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(5L, 6L);
    }

    @Test
    @DisplayName("Должен удалять книгу по id")
    void shouldDeleteBook() {
        var id = dbBooks.get(0).getId();
        assertThat(em.find(Book.class, id)).isNotNull();

        repositoryJpa.deleteById(1L);
        em.flush();
        em.clear();
        assertThat(em.find(Book.class, id)).isNull();
    }


    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }
}


