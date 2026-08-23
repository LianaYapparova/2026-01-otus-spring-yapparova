package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.modelsJpa.Comment;

import java.util.List;
import java.util.Optional;

public interface JpaDataCommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(value = "book-entity-graph")
    Optional<Comment> findById(Long id);

    List<Comment> findByBookId(Long bookId);
}
