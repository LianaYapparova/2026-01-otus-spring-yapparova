package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public String listGenresPage(Model model) {
        List<GenreDto> genreDtos = genreService.findAll().stream()
                .map(GenreDto::fromDomainObject).toList();
        model.addAttribute("genres", genreDtos);
        return "genrelist";
    }
}
