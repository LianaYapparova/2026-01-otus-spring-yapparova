package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping("/api/genres")
    public Flux<GenreDto> listGenresPage() {
        Flux<GenreDto> genreDtos = genreService.findAll()
                .map(GenreDto::fromDomainObject);
        return genreDtos;
    }
}
