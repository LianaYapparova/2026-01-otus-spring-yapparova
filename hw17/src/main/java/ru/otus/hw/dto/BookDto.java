package ru.otus.hw.dto;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;


public record BookDto(long id, String title, AuthorDto author, List<GenreDto> genres) {

    public static BookDto fromDomainObject(Book book) {
        return new BookDto(book.getId(), book.getTitle(), AuthorDto.fromDomainObject((book.getAuthor())
        ), book.getGenres().stream().map(GenreDto::fromDomainObject).toList());
    }

    public String genreAsString() {
        return genres.stream().map(GenreDto::name).collect(Collectors.joining(","));
    }
}