package ru.otus.hw.controller;

public class NotFoundException extends RuntimeException {

    NotFoundException() {
        super("Book not found");
    }
}
