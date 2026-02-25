package ru.otus.hw.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ClasspathResourceLoader implements ResourceLoader {

    @Override
    public InputStream getResourceAsStream(String fileName) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
