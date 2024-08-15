package ru.alexnika.faker.http.server.request;

public enum HttpAccept {
    ANY("*/*"),
    HTML("text/html"),
    JSON("application/json");

    public final String LITERAL;

    HttpAccept(String LITERAL) {
        this.LITERAL = LITERAL;
    }

    public static HttpAccept getBestCompatibleAcceptType(String acceptType) {
        for (HttpAccept type : HttpAccept.values()) {
            if (type.LITERAL.equalsIgnoreCase(acceptType)) {
                return type;
            }
        }
        return ANY;
    }
}
