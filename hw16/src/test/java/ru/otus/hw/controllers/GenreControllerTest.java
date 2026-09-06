package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.GenreController;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldReturnGenresPageWithGenres() throws Exception {
        // given
        Genre genre = new Genre(1L, "Rock");
        when(genreService.findAll()).thenReturn(List.of(genre));

        // when + then
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genrelist"))
                .andExpect(model().attributeExists("genres"));
    }
}
