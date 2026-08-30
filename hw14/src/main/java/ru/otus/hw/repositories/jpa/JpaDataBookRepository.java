package ru.otus.hw.repositories.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.modelsJpa.Book;

import java.util.Optional;

public interface JpaDataBookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author", "genres"})
    Optional<Book> findById(Long id);


    @EntityGraph(attributePaths = {"author", "genres"})
    Page<Book> findAll(Pageable pageable);
}
