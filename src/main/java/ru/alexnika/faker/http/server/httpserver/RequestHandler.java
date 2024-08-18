package ru.alexnika.faker.http.server.httpserver;

import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alexnika.faker.http.server.statistics.Statistics;
import ru.alexnika.faker.http.server.statistics.StatisticsServiceJdbc;

@SuppressWarnings("FieldMayBeFinal")
public class RequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class.getName());
    private Socket clientSocket;
    private Dispatcher dispatcher;
    private final byte[] buffer;

    public RequestHandler(Socket clientSocket, Dispatcher dispatcher) {
        this.clientSocket = clientSocket;
        this.dispatcher = dispatcher;
        this.buffer = new byte[8192];
    }

    public void run() {
        long currentThreadID = Thread.currentThread().threadId();
        String currentThreadState = Thread.currentThread().getState().toString();
        logger.debug("The current thread ID is: {}", currentThreadID);
        logger.debug("The thread with ID {} has state: {}", currentThreadID, currentThreadState);
        try {
            int n = clientSocket.getInputStream().read(buffer);
            if (n < 1) {
                return;
            }
            String rawRequest = new String(buffer, 0, n, StandardCharsets.US_ASCII);
            HttpRequest request = new HttpRequest(rawRequest, clientSocket.getOutputStream());
            request.info();
            Statistics statistics = new Statistics(request, clientSocket);
            if (request.isRequestStatus()) {
                StatisticsServiceJdbc.insert(statistics);
                dispatcher.execute(request, clientSocket.getOutputStream());
            }
            clientSocket.close();
            logger.info("The current clientSocket: {} - closed", clientSocket.toString());
        } catch (IOException | ClassNotFoundException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
