package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BookController {
    @GetMapping("/")
    public String listBookPage() {
        return "booklist";
    }

    @GetMapping("/edit/{id}")
    public String editPage() {
        return "editbook";
    }



    @GetMapping("/create")
    public String createPage() {
        return "createbook";
    }


    @GetMapping("/book/{id}")
    public String bookInfo() {
        return "bookinfo";
    }
}
