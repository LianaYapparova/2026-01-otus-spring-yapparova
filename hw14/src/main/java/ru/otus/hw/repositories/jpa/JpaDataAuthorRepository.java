package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.modelsJpa.Author;


public interface JpaDataAuthorRepository extends JpaRepository<Author, Long> {
}
