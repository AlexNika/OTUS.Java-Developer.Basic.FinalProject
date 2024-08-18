package ru.alexnika.faker.http.server.statistics;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.request.HttpMethod;
import ru.alexnika.faker.http.server.request.HttpProtocol;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.net.Socket;
import java.sql.Timestamp;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class Statistics {
    private Integer id;
    private Timestamp date;
    private HttpMethod method;
    private String uri;
    private HttpProtocol protocol;
    private String remoteAddress;
    private String accept;
    private String contentType;
    private boolean billed;

    public Statistics(@NotNull HttpRequest httpRequest, @NotNull Socket clientSocket) {
        this.date = new Timestamp(System.currentTimeMillis());
        this.method = httpRequest.getMethod();
        this.uri = httpRequest.getOriginalUri();
        this.protocol = httpRequest.getProtocol();
        this.remoteAddress = clientSocket.getInetAddress().getHostAddress();
        this.accept = httpRequest.getHeaders().get("Accept");
        this.contentType = httpRequest.getHeaders().get("Content-Type");
    }

    public Statistics(Integer id, Timestamp date, HttpMethod method, String uri, HttpProtocol protocol, String remoteAddress,
                      String accept, String contentType, boolean billed) {
        this.id = id;
        this.date = date;
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.remoteAddress = remoteAddress;
        this.accept = accept;
        this.contentType = contentType;
        this.billed = billed;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getAccept() {
        return accept;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean getBilled() {
        return billed;
    }

    public void setBilled(boolean billed) {
        this.billed = billed;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "id=" + id +
                ", date=" + date +
                ", method=" + method +
                ", uri='" + uri + '\'' +
                ", protocol=" + protocol +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", accept='" + accept + '\'' +
                ", contentType='" + contentType + '\'' +
                ", billed=" + billed +
                '}';
    }
}