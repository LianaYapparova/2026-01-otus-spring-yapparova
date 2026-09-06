package ru.otus.hw.controller;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Book not found");
    }
}
