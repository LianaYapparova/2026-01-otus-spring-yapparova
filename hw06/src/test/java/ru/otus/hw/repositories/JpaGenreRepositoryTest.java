package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaGenreRepository.class})
class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository genreRepository;

    @Test
    @DisplayName("должен возвращать список всех жанров")
    void shouldReturnAllGenres() {
        var actualGenres = genreRepository.findAll();

        assertThat(actualGenres)
                .isNotNull()
                .isNotEmpty();

        assertThat(actualGenres)
                .extracting(Genre::getId)
                .contains(1L, 2L, 3L);
    }

    @Test
    @DisplayName("должен находить жанры по списку id")
    void shouldFindGenresByIds() {
        var ids = Set.of(1L, 2L, 3L);

        var actualGenres = genreRepository.findAllByIds(ids);

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(3);

        assertThat(actualGenres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("должен возвращать пустой список если id не найдены")
    void shouldReturnEmptyListIfIdsNotFound() {
        var ids = Set.of(999L, 1000L);

        var actualGenres = genreRepository.findAllByIds(ids);

        assertThat(actualGenres).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать частичный список если найдены не все id")
    void shouldReturnPartialListIfSomeIdsNotFound() {
        var ids = Set.of(1L, 2L, 999L);

        var actualGenres = genreRepository.findAllByIds(ids);

        assertThat(actualGenres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }
}
