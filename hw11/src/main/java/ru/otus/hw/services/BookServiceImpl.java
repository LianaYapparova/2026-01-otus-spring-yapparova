package ru.otus.hw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.Readable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final R2dbcEntityTemplate template;
    private final ObjectMapper objectMapper;

    private static final String SQL_ALL = """
                SELECT
                    b.id,
                    b.title,
                    a.id AS author_id,
                    a.full_name AS author_name,
                    json_agg(g.name) AS genres
                FROM books b
                LEFT JOIN authors a ON b.author_id = a.id
                LEFT JOIN books_genres bg ON bg.book_id = b.id
                LEFT JOIN genres g ON g.id = bg.genre_id
                GROUP BY b.id, b.title, a.id, a.full_name
            """;

    private static final String SQL_BY_ID = """
                SELECT
                    b.id,
                    b.title,
                    a.id AS author_id,
                    a.full_name AS author_name,
                    json_agg(g.name) AS genres
                FROM books b
                LEFT JOIN authors a ON b.author_id = a.id
                LEFT JOIN books_genres bg ON bg.book_id = b.id
                LEFT JOIN genres g ON g.id = bg.genre_id
                WHERE b.id = $1
                GROUP BY b.id, b.title, a.id, a.full_name
            """;

    @Transactional(readOnly = true)
    @Override
    public Mono<Book> findById(long id) {
        Mono<Book> bookMono = template.getDatabaseClient().inConnection(connection ->
                Mono.from(connection.createStatement(SQL_BY_ID)
                                .bind("$1", id)
                                .execute())
                        .flatMap(result -> Mono.from(result.map((row, meta) -> mapper(row)))));
        return bookMono;
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<Book> findAll() {
        return template.getDatabaseClient().inConnectionMany(connection ->
                Flux.from(connection.createStatement(SQL_ALL)
                                .execute())
                        .flatMap(result -> result.map(this::mapper)));
    }

    @Transactional
    @Override
    public Mono<Book> insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<Book> update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
       return bookRepository.deleteById(id);
    }

    private Mono<Book> save(long id, String title, long authorId, Set<Long> genresIds) {
        return Mono.defer(() -> {
            if (isEmpty(genresIds)) {
                return Mono.error(new IllegalArgumentException("Genres ids must not be empty"));
            }

             Mono<Author> authorMono = authorRepository.findById(authorId)
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException("Author with id %d not found".formatted(authorId))));

            Mono<List<Genre>>  genresMono = genreRepository.findAllById(genresIds)
                    .collectList()
                    .flatMap(genres -> genresIds.size() == genres.size()
                            ? Mono.just(genres)
                            : Mono.error(new EntityNotFoundException(
                            "One or all genres with ids %s not found".formatted(genresIds))));

            return Mono.zip(authorMono, genresMono)
                    .map(tuple -> new Book(id, title, authorId, tuple.getT1(), tuple.getT2()))
                    .flatMap(bookRepository::save)
                    .flatMap(savedBook -> saveBookGenres(savedBook.getId(), genresIds)
                            .thenReturn(savedBook))
                    .flatMap(this::getAuthorAndGenres);
        });
    }

    private Mono<Void> saveBookGenres(long bookId, Set<Long> genresIds) {
        return template.getDatabaseClient()
                .sql("DELETE FROM books_genres WHERE book_id = $1")
                .bind("$1", bookId)
                .fetch()
                .rowsUpdated()
                .thenMany(Flux.fromIterable(genresIds)
                        .flatMap(genreId -> template.getDatabaseClient()
                                .sql("INSERT INTO books_genres(book_id, genre_id) VALUES ($1, $2)")
                                .bind("$1", bookId)
                                .bind("$2", genreId)
                                .fetch()
                                .rowsUpdated()
                        )
                )
                .then();
    }

    private Mono<Book> getAuthorAndGenres(Book book) {
        var authorMono = authorRepository.findById(book.getAuthorId())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %d not found".formatted(book.getAuthorId()))));

        var genresMono = genreRepository.findAllByBookId(book.getId())
                .collectList();

        return Mono.zip(authorMono, genresMono)
                .map(tuple -> {
                    book.setAuthor(tuple.getT1());
                    book.setGenres(tuple.getT2());
                    return book;
                });
    }

    private Book mapper(Readable selectedRecord) {
        String genres = selectedRecord.get("genres", String.class);
        try {
            List<String> genresList = objectMapper.readValue(genres, new TypeReference<>() {
            });
            return new Book(selectedRecord.get("id", Long.class),
                    selectedRecord.get("title", String.class),
                    selectedRecord.get("author_id", Long.class),
                    new Author(selectedRecord.get("author_id", Long.class), selectedRecord.get("author_name", String.class)),
                    genresList.stream().map(l -> new Genre(0L, l)).toList());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("genres:" + genres + " parsing error:" + e);
        }
    }
}
