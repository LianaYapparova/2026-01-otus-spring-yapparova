package ru.otus.hw.dto;

import ru.otus.hw.models.Comment;

public record CommentDto(long id, String text) {

    public static CommentDto fromDomainObject(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }
}
