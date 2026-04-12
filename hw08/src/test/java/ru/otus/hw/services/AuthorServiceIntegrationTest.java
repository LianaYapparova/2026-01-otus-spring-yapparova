package ru.otus.hw.services;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.MongoDbAuthorsRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataMongoTest
@Import({AuthorServiceImpl.class})
public class AuthorServiceIntegrationTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private MongoDbAuthorsRepository authorRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = authorRepository.findAll();
    }

    @Test
    @DisplayName("Должен возвращать всех авторов")
    @Order(1)
    void shouldReturnAllAuthorsTest() {
        List<Author> authors = authorService.findAll();

        assertThat(authors).isNotEmpty();
        assertThat(authors)
                .extracting(Author::getFullName)
                .containsAll(
                        dbAuthors.stream()
                                .map(Author::getFullName)
                                .toList()
                );
    }

    @Test
    @DisplayName("Должен возвращать пустой список, если авторов нет")
    @Order(2)
    void shouldReturnEmptyListTest() {
        authorRepository.deleteAll();

        List<Author> authors = authorService.findAll();

        assertThat(authors).isEmpty();
    }
}
