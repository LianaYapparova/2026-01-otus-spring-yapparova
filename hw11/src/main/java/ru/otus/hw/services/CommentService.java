package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

public interface CommentService {
    Mono<Comment> findById(long id);

    Flux<Comment> findByBookId(long bookId);

    Mono<Comment> insert(String text, long bookId);

    Mono<Comment> update(long id, String text, long bookId);

    Mono<Void> deleteById(long id);
}
