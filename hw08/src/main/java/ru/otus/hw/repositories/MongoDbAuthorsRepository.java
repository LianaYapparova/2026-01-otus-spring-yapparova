package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Author;

import java.util.List;

public interface MongoDbAuthorsRepository extends MongoRepository<Author, String> {
    List<Author> findAll();
}
