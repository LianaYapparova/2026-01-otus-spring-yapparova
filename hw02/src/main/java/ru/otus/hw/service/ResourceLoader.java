package ru.otus.hw.service;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {
    InputStream getResourceAsStream(String fileName) throws IOException;
}
