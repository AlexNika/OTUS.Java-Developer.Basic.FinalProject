package ru.alexnika.faker.http.server.response;

import ru.alexnika.faker.http.server.request.HttpAccept;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class Response {
    private int statusCode;
    private String codeDescription;
    private HttpAccept acceptType;
    private String responseBody;

    public Response(int statusCode, String codeDescription, HttpAccept acceptType, String responseBody) {
        this.statusCode = statusCode;
        this.codeDescription = codeDescription;
        this.acceptType = acceptType;
        this.responseBody = responseBody;
    }

    public Response(int statusCode, String codeDescription, HttpAccept acceptType) {
        this.statusCode = statusCode;
        this.codeDescription = codeDescription;
        this.acceptType = acceptType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpAccept getAcceptType() {
        return acceptType;
    }
}
