package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.modelsMongo.Genre;

public interface MongoDbGenreRepository extends MongoRepository<Genre, String> {
}
