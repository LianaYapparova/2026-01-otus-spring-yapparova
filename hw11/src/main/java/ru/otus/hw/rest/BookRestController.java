package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    private final CommentService commentService;


    @GetMapping("/api/books")
    public Flux<BookDto> listBookPage() {
        Flux<BookDto> books = bookService.findAll()
                .map(BookDto::fromDomainObject);
        return books;
    }

    @DeleteMapping("/api/book/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteBook(@PathVariable("id") long id) {
        return bookService.deleteById(id);
    }

    @PutMapping("/api/book/{id}")
    public Mono<Void> updateBook(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        List<Long> genreIds = ((List<?>) request.get("genreIds"))
                .stream()
                .map(o -> Long.valueOf(o.toString()))
                .toList();
        return bookService.update(id, (String) request.get("title"), Long.valueOf(request.get("authorId").toString()),
                Set.copyOf(genreIds)).then();
    }

    @GetMapping("/api/book/{id}")
    public Mono<BookDto> bookInfo(@PathVariable("id") long id) {
        return bookService.findById(id)
                .map(BookDto::fromDomainObject)
                .onErrorMap(EntityNotFoundException.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
    }

    @GetMapping("/api/books/{bookId}/comments")
    public Flux<CommentDto> comments(@PathVariable("bookId") long bookId) {
        Flux<CommentDto> commentDtos = commentService.findByBookId(bookId)
                .map(CommentDto::fromDomainObject);
        return commentDtos;
    }

    @PostMapping("/api/book")
    public Mono<Void> saveBook(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        Long authorId = Long.valueOf(request.get("authorId").toString());
        List<Long> genreIds = ((List<?>) request.get("genreIds"))
                .stream()
                .map(o -> Long.valueOf(o.toString()))
                .toList();
        return title.isBlank()
                ? Mono.empty()
                : bookService.insert(title, authorId, new HashSet<>(genreIds)).then();
    }
}
