package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public String listAuthorPage(Model model) {
        List<AuthorDto> authorDtos = authorService.findAll().stream()
                .map(AuthorDto::fromDomainObject).toList();
        model.addAttribute("authors", authorDtos);
        return "authorslist";
    }
}
