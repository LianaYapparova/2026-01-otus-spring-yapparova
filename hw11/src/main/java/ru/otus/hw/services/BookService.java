package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

import java.util.Set;

public interface BookService {
    Mono<Book> findById(long id);

    Flux<Book> findAll();

    Mono<Book> insert(String title, long authorId, Set<Long> genresIds);

    Mono<Book> update(long id, String title, long authorId, Set<Long> genresIds);

    Mono<Void> deleteById(long id);
}
