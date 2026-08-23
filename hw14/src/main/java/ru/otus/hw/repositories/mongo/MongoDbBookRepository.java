package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.modelsMongo.Book;

public interface MongoDbBookRepository extends MongoRepository<Book, String>{

}
