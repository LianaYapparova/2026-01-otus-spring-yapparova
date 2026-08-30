package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.modelsJpa.Genre;

public interface JpaDataGenreRepository extends JpaRepository<Genre, Long> {
}
