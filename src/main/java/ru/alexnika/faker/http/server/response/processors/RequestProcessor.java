package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestProcessor {
    void execute(HttpRequest request, OutputStream out) throws IOException;
}