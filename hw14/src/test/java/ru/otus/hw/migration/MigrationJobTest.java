package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import ru.otus.hw.modelsJpa.Author;
import ru.otus.hw.modelsJpa.Book;
import ru.otus.hw.modelsJpa.Comment;
import ru.otus.hw.modelsJpa.Genre;

import ru.otus.hw.repositories.jpa.JpaDataAuthorRepository;
import ru.otus.hw.repositories.jpa.JpaDataBookRepository;
import ru.otus.hw.repositories.jpa.JpaDataCommentRepository;
import ru.otus.hw.repositories.jpa.JpaDataGenreRepository;

import ru.otus.hw.repositories.mongo.MongoDbAuthorsRepository;
import ru.otus.hw.repositories.mongo.MongoDbBookRepository;
import ru.otus.hw.repositories.mongo.MongoDbCommentRepository;
import ru.otus.hw.repositories.mongo.MongoDbGenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
class MigrationJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;


    @Autowired
    private JpaDataAuthorRepository jpaAuthorRepository;

    @Autowired
    private JpaDataGenreRepository jpaGenreRepository;

    @Autowired
    private JpaDataBookRepository jpaBookRepository;

    @Autowired
    private JpaDataCommentRepository jpaCommentRepository;

    @Autowired
    private MongoDbAuthorsRepository mongoAuthorRepository;

    @Autowired
    private MongoDbGenreRepository mongoGenreRepository;

    @Autowired
    private MongoDbBookRepository mongoBookRepository;

    @Autowired
    private MongoDbCommentRepository mongoCommentRepository;


    @BeforeEach
    void setUp() {

        jobRepositoryTestUtils.removeJobExecutions();

        mongoCommentRepository.deleteAll();
        mongoBookRepository.deleteAll();
        mongoGenreRepository.deleteAll();
        mongoAuthorRepository.deleteAll();

        jpaCommentRepository.deleteAll();
        jpaBookRepository.deleteAll();
        jpaGenreRepository.deleteAll();
        jpaAuthorRepository.deleteAll();
    }


    @Test
    void shouldMigrateDataFromPostgresToMongo() throws Exception {

        Author author = jpaAuthorRepository.save(new Author(0, "Лев Толстой"));

        Genre genre = jpaGenreRepository.save(new Genre(0, "Роман"));

        Book book = new Book();

        book.setTitle("Война и мир");
        book.setAuthor(author);
        book.setGenres(List.of(genre));

        book = jpaBookRepository.save(book);

        Comment comment = new Comment();

        comment.setText("Отличная книга");
        comment.setBook(book);

        jpaCommentRepository.save(comment);


        JobParameters parameters =
                new JobParametersBuilder()
                        .addLong("testRun", System.currentTimeMillis())
                        .toJobParameters();

        JobExecution execution =
                jobLauncherTestUtils.launchJob(parameters);

        assertThat(execution.getExitStatus().getExitCode())
                .isEqualTo("COMPLETED");


        var mongoAuthors = mongoAuthorRepository.findAll();

        assertThat(mongoAuthors).hasSize(1);

        assertThat(mongoAuthors.get(0).getId()).isEqualTo(String.valueOf(author.getId()));

        assertThat(mongoAuthors.get(0).getFullName()).isEqualTo("Лев Толстой");


        var mongoGenres = mongoGenreRepository.findAll();

        assertThat(mongoGenres)
                .hasSize(1);

        assertThat(mongoGenres.get(0).getId())
                .isEqualTo(String.valueOf(genre.getId()));

        assertThat(mongoGenres.get(0).getName())
                .isEqualTo("Роман");

        var mongoBooks =
                mongoBookRepository.findAll();

        assertThat(mongoBooks).hasSize(1);

        var mongoBook = mongoBooks.get(0);

        assertThat(mongoBook.getId())
                .isEqualTo(String.valueOf(book.getId()));

        assertThat(mongoBook.getTitle())
                .isEqualTo("Война и мир");

        assertThat(mongoBook.getAuthor())
                .isNotNull();

        assertThat(mongoBook.getAuthor().getId())
                .isEqualTo(String.valueOf(author.getId()));

        assertThat(mongoBook.getGenres())
                .hasSize(1);

        assertThat(mongoBook.getGenres().get(0).getId())
                .isEqualTo(String.valueOf(genre.getId()));

        var mongoComments =
                mongoCommentRepository.findAll();

        assertThat(mongoComments)
                .hasSize(1);

        assertThat(mongoComments.get(0).getText())
                .isEqualTo("Отличная книга");

        assertThat(mongoComments.get(0).getBookId())
                .isEqualTo(String.valueOf(book.getId()));
    }
}