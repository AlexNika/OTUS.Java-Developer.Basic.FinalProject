package ru.alexnika.faker.http.server.exceptions;

import java.time.LocalDateTime;

public class DefaultErrorDto {
    private String code;
    private String message;
    private String date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DefaultErrorDto() {
    }

    public DefaultErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
        this.date = LocalDateTime.now().toString();
    }
}
