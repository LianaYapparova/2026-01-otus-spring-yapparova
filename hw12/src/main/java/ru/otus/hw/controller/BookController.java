package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;


    @GetMapping("/")
    public String listBookPage(Model model) {
        List<BookDto> books = bookService.findAll().stream()
                .map(BookDto::fromDomainObject).toList();
        model.addAttribute("books", books);
        return "booklist";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        BookDto bookDto = bookService.findById(id)
                .map(BookDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
        List<AuthorDto> authorDtos = authorService.findAll().stream()
                .map(AuthorDto::fromDomainObject).toList();
        List<GenreDto> genreDtos = genreService.findAll().stream()
                .map(GenreDto::fromDomainObject).toList();
        model.addAttribute("genres", genreDtos);
        model.addAttribute("authors", authorDtos);
        model.addAttribute("book", bookDto);
        model.addAttribute("presetGenreId", bookDto.genres().get(0).id());
        return "editbook";
    }

    @PostMapping("/edit")
    public String updateBook(@Valid @ModelAttribute("book") BookDto bookDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "genreIds", defaultValue = "") List<Long> genreIds) {
        if (bindingResult.hasErrors()) {
            return "editbook";
        }
        bookService.update(bookDto.id(), bookDto.title(), bookDto.author().id(), Set.of(genreIds.toArray(new Long[0])));
        return "redirect:/";
    }

    @PostMapping("/create")
    public String saveBook(@RequestParam(value = "title") String title,
                           @RequestParam(value = "authorId") Long authorId,
                           @RequestParam(value = "genreIds") List<Long> genreIds) {
        if (!title.isBlank()) {
            bookService.insert(title, authorId, Set.of(genreIds.toArray(new Long[0])));
        }
        return "redirect:/";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        List<AuthorDto> authorDtos = authorService.findAll().stream()
                .map(AuthorDto::fromDomainObject).toList();
        List<GenreDto> genreDtos = genreService.findAll().stream()
                .map(GenreDto::fromDomainObject).toList();
        model.addAttribute("genres", genreDtos);
        model.addAttribute("authors", authorDtos);
        model.addAttribute("presetGenreId", genreDtos.get(0).id());
        return "createbook";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/book")
    public String bookInfo(@RequestParam("id") long id, Model model) {
        BookDto bookDto = bookService.findById(id)
                .map(BookDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);

        List<CommentDto> commentDtos = commentService.findByBookId(id)
                .stream()
                .map(CommentDto::fromDomainObject)
                .toList();
        model.addAttribute("book", bookDto);
        model.addAttribute("comments", commentDtos);
        return "bookinfo";
    }
}
