package ru.otus.hw.repositories;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {

    Mono<Comment> findById(Long id);

    @Query("SELECT c.id, c.text FROM comments c WHERE c.book_id = $1")
    Flux<Comment> findAllByBookId(Long bookId);
}
