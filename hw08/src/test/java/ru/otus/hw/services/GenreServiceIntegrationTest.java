package ru.otus.hw.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoDbGenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataMongoTest
@Import({GenreServiceImpl.class})
class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private MongoDbGenreRepository genreRepository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = genreRepository.findAll();
    }

    @Test
    @DisplayName("Должен возвращать все жанры")
    @Order(1)
    void shouldReturnAllGenresTest() {
        List<Genre> genres = genreService.findAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres)
                .extracting(Genre::getName)
                .containsAll(
                        dbGenres.stream()
                                .map(Genre::getName)
                                .toList()
                );
    }

    @Test
    @DisplayName("Должен возвращать пустой список, если жанров нет")
    @Order(2)
    void shouldReturnEmptyListTest() {
        genreRepository.deleteAll();

        List<Genre> genres = genreService.findAll();

        assertThat(genres).isEmpty();
    }
}
