package ru.otus.hw.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private R2dbcEntityTemplate template;


    @Test
    void shouldSetIdOnSaveTest() {
        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthorId(1L);

        Mono<Book> bookMono = bookRepository.save(book);

        StepVerifier
                .create(bookMono)
                .assertNext(savedBook -> {
                    assertNotNull(savedBook.getId());
                    assertEquals("Война и мир", savedBook.getTitle());
                    assertEquals(1L, savedBook.getAuthorId());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindByIdTest() {
        Book book = new Book();
        book.setTitle("Преступление и наказание");
        book.setAuthorId(2L);

        Mono<Book> savedBookMono = bookRepository.save(book);

        Mono<Book> foundBookMono = savedBookMono
                .flatMap(savedBook -> bookRepository.findById(savedBook.getId()));

        StepVerifier
                .create(foundBookMono)
                .assertNext(foundBook -> {
                    assertNotNull(foundBook.getId());
                    assertEquals("Преступление и наказание", foundBook.getTitle());
                    assertEquals(2L, foundBook.getAuthorId());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindByIdNotFoundTest() {
        Mono<Book> bookMono = bookRepository.findById(999L);

        StepVerifier
                .create(bookMono)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindAllTest() {
        // Очищаем таблицу перед тестом
        template.getDatabaseClient()
                .sql("DELETE FROM books")
                .fetch()
                .rowsUpdated()
                .block();

        Book book1 = new Book();
        book1.setTitle("Мастер и Маргарита");
        book1.setAuthorId(3L);

        Book book2 = new Book();
        book2.setTitle("Идиот");
        book2.setAuthorId(2L);

        Flux<Book> savedBooks = bookRepository.saveAll(List.of(book1, book2));

        Mono<List<Book>> allBooksMono = savedBooks
                .collectList()
                .flatMapMany(saved -> bookRepository.findAll())
                .collectList();

        StepVerifier
                .create(allBooksMono)
                .assertNext(books -> {
                    assertNotNull(books);
                    assertTrue(books.size() >= 2);

                    // Проверяем, что все сохраненные книги есть в результате
                    List<String> titles = books.stream()
                            .map(Book::getTitle)
                            .collect(Collectors.toList());

                    assertTrue(titles.contains("Мастер и Маргарита"));
                    assertTrue(titles.contains("Идиот"));
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldUpdateBookTest() {

        Book book = new Book();
        book.setTitle("Старая книга");
        book.setAuthorId(1L);

        Mono<Book> updatedBookMono = bookRepository.save(book)
                .flatMap(savedBook -> {
                    savedBook.setTitle("Новая книга");
                    savedBook.setAuthorId(2L);
                    return bookRepository.save(savedBook);
                });

        StepVerifier
                .create(updatedBookMono)
                .assertNext(updatedBook -> {
                    assertNotNull(updatedBook.getId());
                    assertEquals("Новая книга", updatedBook.getTitle());
                    assertEquals(2L, updatedBook.getAuthorId());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldDeleteByIdTest() {
        Book book = new Book();
        book.setTitle("Книга для удаления");
        book.setAuthorId(1L);


        Mono<Void> deleteAndVerifyMono = bookRepository.save(book)
                .flatMap(savedBook -> {
                    Long id = savedBook.getId();
                    return bookRepository.deleteById(id)
                            .then(bookRepository.findById(id))
                            .doOnNext(found -> {
                                assertNull(found, "Книга должна быть удалена");
                            });
                })
                .then();

        StepVerifier
                .create(deleteAndVerifyMono)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldSaveBookWithNullAuthorTest() {
        Book book = new Book();
        book.setTitle("Книга без автора");
        book.setAuthorId(null);

        Mono<Book> bookMono = bookRepository.save(book);

        StepVerifier
                .create(bookMono)
                .assertNext(savedBook -> {
                    assertNotNull(savedBook.getId());
                    assertEquals("Книга без автора", savedBook.getTitle());
                    assertNull(savedBook.getAuthorId());
                })
                .expectComplete()
                .verify();
    }
}
