package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.MongoDbBookRepository;
import ru.otus.hw.repositories.MongoDbCommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@Import({CommentServiceImpl.class})
public class CommentServiceIntegrationTest {
    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private MongoDbCommentRepository commentRepository;

    @Autowired
    private MongoDbBookRepository bookRepository;

    private List<Book> dbBooks;
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbBooks = bookRepository.findAll();
        dbComments = commentRepository.findByBookId(dbBooks.get(0).getId());
    }

    @Test
    @DisplayName("Должен загружать комментарий без LazyInitializationException")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldLoadCommentWithoutLazyException() {
        var comment = commentService.findById(dbComments.get(0).getId()).orElseThrow();

        assertThatCode(() -> {
            comment.getText();
            comment.getBookId();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен загружать комментарии по книге")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldFindByBookId() {
        String bookId = dbBooks.get(0).getId();

        List<Comment> comments = commentService.findByBookId(bookId);

        assertThat(comments).isNotEmpty();
        assertThatCode(() ->
                comments.forEach(c -> {
                    assertThat(c.getText()).isNotBlank();
                    assertThat(c.getBookId()).isNotNull();
                })
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен вставлять комментарий")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldInsertComment() {
        String bookId = dbBooks.get(0).getId();

        Comment saved = commentService.insert("new comment", bookId);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo("new comment");
        assertThat(saved.getBookId()).isEqualTo(bookId);

        Comment found = commentRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getText()).isEqualTo("new comment");
    }

    @Test
    @DisplayName("Должен кидать исключение при вставке с несуществующей книгой")
    void shouldThrowWhenInsertWithWrongBook() {
        assertThatThrownBy(() ->
                commentService.insert("text", "book8")
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Обновление комментария")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldUpdateComment() {
        String  newText = "new comment";
        Comment existing = dbComments.get(0);
        String bookId = dbBooks.get(0).getId();

        Comment updated = commentService.update(existing.getId(), newText, bookId);

        assertThat(updated.getId()).isEqualTo(existing.getId());
        assertThat(updated.getText()).isEqualTo(newText);
        assertThat(updated.getBookId()).isEqualTo(bookId);

        Comment found = commentRepository.findById(existing.getId()).orElseThrow();
        assertThat(found.getText()).isEqualTo(newText);
    }

    @Test
    @DisplayName("Удаление комментария по ID")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldDeleteComment() {
        Comment comment = dbComments.get(0);

        commentService.deleteById(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
