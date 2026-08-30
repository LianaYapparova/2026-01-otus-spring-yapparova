package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.modelsMongo.Author;

public interface MongoDbAuthorsRepository extends MongoRepository<Author, String> {
}
