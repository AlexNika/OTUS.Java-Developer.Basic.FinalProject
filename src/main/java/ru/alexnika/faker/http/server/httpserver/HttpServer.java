package ru.alexnika.faker.http.server.httpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getName());
    private final ExecutorService vte;
    private final int serverPort;
    private final int serverBacklog;
    private final InetAddress serverIpAddress;

    public HttpServer(int serverPort, int serverBacklog, String ipAddress) throws UnknownHostException {
        this.serverPort = serverPort;
        this.serverBacklog = serverBacklog;
        this.serverIpAddress = InetAddress.getByName(ipAddress);
        this.vte = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.serverPort, this.serverBacklog, this.serverIpAddress)) {
            logger.info("The HTTP server started and listening for connection by address: {}:{}",
                    this.serverIpAddress, this.serverPort);
            try {
                Dispatcher dispatcher = new Dispatcher();
                while (serverSocket.isBound() && !serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Received connection from {}", clientSocket.toString());
                    vte.execute(new RequestHandler(clientSocket, dispatcher));
                }
            } catch (IOException | RejectedExecutionException e) {
                logger.error(e);
            }
        } catch (IOException e) {
            logger.error("Problem with setting server socket", e);
        }
    }
}