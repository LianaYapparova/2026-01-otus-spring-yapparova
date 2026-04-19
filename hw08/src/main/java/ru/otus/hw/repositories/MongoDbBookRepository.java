package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;

public interface MongoDbBookRepository extends MongoRepository<Book, String>{

}
