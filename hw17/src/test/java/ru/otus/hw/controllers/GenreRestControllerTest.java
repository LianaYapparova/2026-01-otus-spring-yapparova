package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.rest.GenreRestController;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldReturnCorrectGenresList() throws Exception {
        List<Genre> genres = List.of(
                new Genre(1, "Genre1"),
                new Genre(2, "Genre2")
        );

        given(genreService.findAll()).willReturn(genres);

        List<GenreDto> expectedResult = genres.stream()
                .map(GenreDto::fromDomainObject)
                .toList();

        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));
    }

    @Test
    void shouldReturnEmptyListWhenNoGenres() throws Exception {
        given(genreService.findAll()).willReturn(List.of());

        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
