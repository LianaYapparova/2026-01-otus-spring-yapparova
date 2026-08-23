package ru.otus.hw.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
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

import java.util.Map;

@Configuration
public class MigrationJobConfig {

    @Bean
    public RepositoryItemReader<Author> authorReader(JpaDataAuthorRepository authorRepository) {
        return new RepositoryItemReaderBuilder<Author>()
                .name("authorReader")
                .repository(authorRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Author, ru.otus.hw.modelsMongo.Author> authorProcessor() {
        return source ->
                ru.otus.hw.modelsMongo.Author.builder()
                        .id(String.valueOf(source.getId()))
                        .fullName(source.getFullName())
                        .build();
    }

    @Bean
    public ItemWriter<ru.otus.hw.modelsMongo.Author> authorWriter(MongoDbAuthorsRepository repository) {
        return chunk ->
                repository.saveAll(chunk.getItems());
    }

    @Bean
    public Step migrateAuthorsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                   RepositoryItemReader<Author> authorReader,
                                   ItemProcessor<Author, ru.otus.hw.modelsMongo.Author> authorProcessor,
                                   ItemWriter<ru.otus.hw.modelsMongo.Author> authorWriter) {
        return new StepBuilder("migrateAuthors", jobRepository)
                .<Author, ru.otus.hw.modelsMongo.Author>chunk(100, transactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }


    @Bean
    public RepositoryItemReader<Genre> genreReader(JpaDataGenreRepository repository) {
        return new RepositoryItemReaderBuilder<Genre>()
                .name("genreReader")
                .repository(repository)
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Genre, ru.otus.hw.modelsMongo.Genre> genreProcessor() {
        return source ->
                ru.otus.hw.modelsMongo.Genre.builder()
                        .id(String.valueOf(source.getId()))
                        .name(source.getName())
                        .build();
    }

    @Bean
    public ItemWriter<ru.otus.hw.modelsMongo.Genre> genreWriter(MongoDbGenreRepository repository) {
        return chunk ->
                repository.saveAll(chunk.getItems());
    }

    @Bean
    public Step migrateGenresStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<Genre> genreReader, ItemProcessor<Genre, ru.otus.hw.modelsMongo.Genre> genreProcessor,
                                  ItemWriter<ru.otus.hw.modelsMongo.Genre> genreWriter) {
        return new StepBuilder("migrateGenres", jobRepository)
                .<Genre, ru.otus.hw.modelsMongo.Genre>chunk(100, transactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public RepositoryItemReader<Book> bookReader(JpaDataBookRepository repository) {
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(repository)
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Book, ru.otus.hw.modelsMongo.Book> bookProcessor() {
        return source -> {
            var author =
                    ru.otus.hw.modelsMongo.Author.builder()
                            .id(String.valueOf(source.getAuthor().getId()))
                            .fullName(source.getAuthor().getFullName())
                            .build();

            var genres =
                    source.getGenres()
                            .stream()
                            .map(genre ->
                                    ru.otus.hw.modelsMongo.Genre.builder()
                                            .id(String.valueOf(genre.getId()))
                                            .name(genre.getName())
                                            .build()
                            )
                            .toList();

            return ru.otus.hw.modelsMongo.Book.builder()
                    .id(String.valueOf(source.getId()))
                    .title(source.getTitle())
                    .author(author)
                    .genres(genres)
                    .build();
        };
    }

    @Bean
    public ItemWriter<ru.otus.hw.modelsMongo.Book> bookWriter(MongoDbBookRepository repository) {
        return chunk ->
                repository.saveAll(chunk.getItems());
    }

    @Bean
    public Step migrateBooksStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                 RepositoryItemReader<Book> bookReader, ItemProcessor<Book, ru.otus.hw.modelsMongo.Book> bookProcessor,
                                 ItemWriter<ru.otus.hw.modelsMongo.Book> bookWriter) {
        return new StepBuilder("migrateBooks", jobRepository)
                .<Book, ru.otus.hw.modelsMongo.Book>chunk(100, transactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }


    @Bean
    public RepositoryItemReader<Comment> commentReader(JpaDataCommentRepository repository) {
        return new RepositoryItemReaderBuilder<Comment>()
                .name("commentReader")
                .repository(repository)
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Comment, ru.otus.hw.modelsMongo.Comment> commentProcessor() {
        return source ->
                ru.otus.hw.modelsMongo.Comment.builder()
                        .id(String.valueOf(source.getId()))
                        .text(source.getText())
                        .bookId(String.valueOf(source.getBook().getId()))
                        .build();
    }

    @Bean
    public ItemWriter<ru.otus.hw.modelsMongo.Comment> commentWriter(MongoDbCommentRepository repository) {
        return chunk ->
                repository.saveAll(chunk.getItems());
    }

    @Bean
    public Step migrateCommentsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                    RepositoryItemReader<Comment> commentReader, ItemProcessor<Comment, ru.otus.hw.modelsMongo.Comment> commentProcessor,
                                    ItemWriter<ru.otus.hw.modelsMongo.Comment> commentWriter) {
        return new StepBuilder("migrateComments", jobRepository)
                .<Comment, ru.otus.hw.modelsMongo.Comment>chunk(100, transactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
                .build();
    }

    @Bean
    public Job migrationJob(JobRepository jobRepository, Step migrateAuthorsStep,
            Step migrateGenresStep, Step migrateBooksStep, Step migrateCommentsStep) {
        return new JobBuilder("migrationJob", jobRepository)
                .start(migrateAuthorsStep)
                .next(migrateGenresStep)
                .next(migrateBooksStep)
                .next(migrateCommentsStep)
                .build();
    }
}
