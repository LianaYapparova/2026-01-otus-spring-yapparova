package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Mono<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<Comment> findByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Transactional
    @Override
    public Mono<Comment> insert(String text, long bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> commentRepository.save(new Comment(0, text, book)));
    }

    @Transactional
    @Override
    public Mono<Comment> update(long id, String text, long bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> commentRepository.save(new Comment(id, text, book)));
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        return commentRepository.deleteById(id);
    }
}
