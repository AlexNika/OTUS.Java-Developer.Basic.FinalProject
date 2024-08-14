package ru.alexnika.faker.http.server.exceptions;

public class BadHttpProtocolException extends Exception {
    public BadHttpProtocolException(String message) {
        super(message);
    }
}
