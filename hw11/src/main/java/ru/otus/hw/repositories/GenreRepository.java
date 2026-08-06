package ru.otus.hw.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

public interface GenreRepository extends ReactiveCrudRepository<Genre, Long> {

    @Query("""
            select g.id, g.name
            from genres g
            join books_genres bg on g.id = bg.genre_id
            where bg.book_id = :bookId
            """)
    Flux<Genre> findAllByBookId(Long bookId);
}
