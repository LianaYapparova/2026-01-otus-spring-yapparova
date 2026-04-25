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
import ru.otus.hw.controller.NotFoundException;
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


    @GetMapping("/api/book")
    public List<BookDto> listBookPage() {
        List<BookDto> books = bookService.findAll().stream()
                .map(BookDto::fromDomainObject).toList();
        return books;
    }

    @DeleteMapping("/api/book/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/book/{id}")
    public void updateBook(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        List<Long> genreIds = ((List<?>) request.get("genreIds"))
                .stream()
                .map(o -> Long.valueOf(o.toString()))
                .toList();
        bookService.update(id, (String) request.get("title"), Long.valueOf(request.get("authorId").toString()),
                Set.copyOf(genreIds));
    }

    @GetMapping("/api/book/{id}")
    public BookDto bookInfo(@PathVariable("id") long id) {
        BookDto bookDto = bookService.findById(id)
                .map(BookDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
        return bookDto;
    }

    @GetMapping("/api/comment/{id}")
    public List<CommentDto> comments(@PathVariable("id") long id) {
        List<CommentDto> commentDtos = commentService.findByBookId(id)
                .stream()
                .map(CommentDto::fromDomainObject)
                .toList();
        return commentDtos;
    }

    @PostMapping("/api/book")
    public void saveBook(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        Integer authorId = (Integer) request.get("authorId");
        List<Long> genreIds = (List<Long>) request.get("genreIds");
        if (!title.isBlank()) {
            bookService.insert(title, authorId, new HashSet<>(genreIds));
        }
    }
}
