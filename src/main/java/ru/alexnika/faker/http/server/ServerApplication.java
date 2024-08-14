package ru.alexnika.faker.http.server;

import ru.alexnika.faker.http.server.config.Config;
import ru.alexnika.faker.http.server.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;

public class ServerApplication {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class.getName());

    public static void main(String[] args) throws UnknownHostException {
        int port = 8080;
        int backlog = 50;
        try {
            port = Integer.parseInt(Config.getProperty("server.port"));
            backlog = Integer.parseInt(Config.getProperty("server.backlog"));

        } catch (NumberFormatException e) {
            logger.warn("Parameter 'server.port' or 'server.backlog' in file 'config.properties'\n" +
                    "must be a string consisting only of digits", e);
            logger.warn("The default 'port' value is 8080");
            logger.warn("The default 'backlog' value is 50");
        }
        String ipaddress = Config.getProperty("server.ipaddress");
        new HttpServer(port, backlog, ipaddress).start();
    }
}
