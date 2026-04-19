package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Genre;

public interface MongoDbGenreRepository extends MongoRepository<Genre, String> {
}
