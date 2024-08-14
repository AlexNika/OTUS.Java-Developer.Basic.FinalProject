package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestProcessor {
    void execute(HttpRequestParser request, OutputStream out) throws IOException;
}