package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface MongoDbCommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByBookId(String bookId);

    void deleteCommentsByBookId(String bookId);
}
