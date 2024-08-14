package ru.alexnika.faker.http.server.processors;

public class TemplateRequestPreprocessor {
    private final String CRLF = System.lineSeparator();
//    TODO переделать на lineSeparator и сделать констурктор ответа сервера http response
//    TODO также сделать vararg с передачей в метод любого кол-ва параметров

    public String prepareResponse(int statusCode, String statusText, String contentType, String body) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                Content-Type: %s\r
                \r
                %s
                """;
        return (responseTemplate.formatted(statusCode, statusText, contentType, body));
    }

    public String prepareResponseWithoutBody(int statusCode, String statusText) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                \r
                """;
        return (responseTemplate.formatted(statusCode, statusText));
    }

    public String prepareOptionsResponse(int statusCode, String statusText, String localDate, String allowedMethods,
                                         String accessControlAllowOrigin, String accessControlAllowMethods,
                                         String accessControlAllowHeaders) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                Date: %s\r
                Allow: %s\r
                Access-Control-Allow-Origin: %s\r
                Access-Control-Allow-Methods: %s\r
                Access-Control-Allow-Headers: %s\r
                \r
                """;
        return (responseTemplate.formatted(statusCode, statusText, localDate, allowedMethods, accessControlAllowOrigin,
                accessControlAllowMethods, accessControlAllowHeaders));
    }
}
