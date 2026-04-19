package ru.otus.hw.dto;


import ru.otus.hw.models.Author;

public record AuthorDto(long id, String fullName) {

    public Author toDomainObject() {
        return new Author(id, fullName);
    }

    public static AuthorDto fromDomainObject(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}