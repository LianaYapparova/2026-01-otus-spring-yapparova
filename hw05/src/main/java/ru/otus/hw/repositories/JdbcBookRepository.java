package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;


@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
                SELECT 
                    b.id,
                    b.title,
                    a.id AS author_id,
                    a.full_name AS author_name,
                    g.id AS genre_id,
                    g.name AS genre_name
                FROM books b
                LEFT JOIN authors a ON b.author_id = a.id
                LEFT JOIN books_genres bg ON bg.book_id = b.id
                LEFT JOIN genres g ON g.id = bg.genre_id
                WHERE b.id = :id
                """;
        Map<String, Object> params = Map.of("id", id);
        Book book = namedParameterJdbcOperations.query(sql, params, new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var book = findById(id);
        removeGenresRelationsFor(book.orElseThrow(() -> new EntityNotFoundException("Book with id %d not found")));
        namedParameterJdbcOperations.update(
                "delete from books where id = :id", Collections.singletonMap("id", id)
        );
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                SELECT 
                    b.id,
                    b.title,
                    a.id AS author_id,
                    a.full_name AS author_name
                FROM books b
                LEFT JOIN authors a ON b.author_id = a.id
                """;
        return namedParameterJdbcOperations.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query("SELECT book_id, genre_id FROM books_genres",
                new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        booksWithoutGenres.forEach(book -> {
            List<Genre> genreList = new ArrayList<>();
            for (BookGenreRelation relation : relations) {
                if (relation.bookId == book.getId()) {
                    fillGenreList(genres, relation, genreList);
                }
            }
            book.setGenres(genreList);
        });
    }

    private void fillGenreList(List<Genre> genres, BookGenreRelation relation, List<Genre> genreList) {
        for (Genre genre : genres) {
            if (genre.getId() == relation.genreId) {
                genreList.add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        namedParameterJdbcOperations.update("insert into books (title, author_id) values (:title, :author_id)",
                params, keyHolder);

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = """
                UPDATE books
                SET title = :title,
                author_id = :author_id
                WHERE id = :id
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        int count = namedParameterJdbcOperations.update(sql, params);

        if (count <= 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        var bookById = findById(book.getId());
        removeGenresRelationsFor(bookById.orElseThrow(() ->
                new EntityNotFoundException("Book with id %d not found".formatted(book.getId()))));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String sql = """
                insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)
                """;
        SqlParameterSource[] batchParams = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("book_id", book.getId())
                        .addValue("genre_id", genre.getId()))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcOperations.batchUpdate(sql, batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = """
                delete from books_genres where  book_id = :bookId AND genre_id = :genreId
                """;
        SqlParameterSource[] batchParams = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("bookId", book.getId())
                        .addValue("genreId", genre.getId()))
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcOperations.batchUpdate(sql, batchParams);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("author_name"));
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(author);
            return book;
        }
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                if (book == null) {
                    book = new Book();
                    book.setId(rs.getLong("id"));
                    book.setTitle(rs.getString("title"));
                    Author author = new Author();
                    author.setId(rs.getLong("author_id"));
                    author.setFullName(rs.getString("author_name"));
                    book.setAuthor(author);
                }

                if (!rs.wasNull()) {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("genre_name"));
                    genres.add(genre);
                }
                book.setGenres(genres);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
