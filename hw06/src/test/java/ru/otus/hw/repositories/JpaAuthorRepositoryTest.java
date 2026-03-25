package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({JpaAuthorRepository.class})
public class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;


    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }


    @Test
    @DisplayName("Должен возвращать список всех авторов")
    void shouldReturnAllAuthors() {
        var actualAuthors = authorRepository.findAll();
        var expectedAuthors = dbAuthors;

        assertThat(actualAuthors)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedAuthors);
    }

    @Test
    @DisplayName("Должен находить автора по id")
    void shouldFindAuthorById() {
        var expectedAuthor = em.find(Author.class, 1L);

        var actualAuthor = authorRepository.findById(1L);

        assertThat(actualAuthor)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Должен возвращать пусто если автор не найден")
    void shouldReturnEmptyIfAuthorNotFound() {
        var actualAuthor = authorRepository.findById(999L);

        assertThat(actualAuthor).isEmpty();
    }
}
