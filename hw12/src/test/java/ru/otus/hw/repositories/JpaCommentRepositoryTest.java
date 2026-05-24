package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JpaCommentRepositoryTest {
    @Autowired
    private JpaDataCommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        var expectedComment = em.find(Comment.class, 1L);

        var actualComment = commentRepository.findById(1L);

        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен возвращать пусто если комментарий не найден")
    void shouldReturnEmptyIfCommentNotFound() {
        var actualComment = commentRepository.findById(999L);

        assertThat(actualComment).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать комментарии по id книги")
    void shouldFindCommentsByBookId() {
        var actualComments = commentRepository.findByBookId(1L);

        assertThat(actualComments)
                .isNotNull()
                .isNotEmpty();

        assertThat(actualComments)
                .allMatch(comment -> comment.getBook().getId() == 1L);
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldSaveNewComment() {
        var book = em.find(Book.class, 1L);

        var newComment = new Comment(0, "New comment", book);

        var savedComment = commentRepository.save(newComment);

        var actualComment = em.find(Comment.class, savedComment.getId());

        assertThat(actualComment)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(newComment);
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = em.find(Comment.class, 1L);

        comment.setText("Updated text");

        commentRepository.save(comment);
        em.flush();
        em.clear();

        var updatedComment = em.find(Comment.class, 1L);

        assertThat(updatedComment.getText()).isEqualTo("Updated text");
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteCommentById() {
        assertThat(em.find(Comment.class, 1L)).isNotNull();

        commentRepository.deleteById(1L);
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, 1L)).isNull();
    }
}
