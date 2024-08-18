package ru.alexnika.faker.http.server.statistics.DBStorage;

import ru.alexnika.faker.http.server.config.Config;
import ru.alexnika.faker.http.server.exceptions.NoDBConfigException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class.getName());
    private final String DB_URL;
    private final String DB_LOGIN;
    private final String DB_PASSWORD;

    public DBConnection() throws NoDBConfigException {
        String url = Config.getProperty("db.uri");
        String login = Config.getProperty("db.login");
        String password = System.getenv("db.password");
        if ((url == null) || (login == null) || (password == null)) {
            throw new NoDBConfigException();
        }
        DB_URL = url;
        DB_LOGIN = login;
        DB_PASSWORD = password;
        logger.info("DBConnection have been initialized");
    }

    public String getDB_URL() {
        return DB_URL;
    }

    public String getDB_LOGIN() {
        return DB_LOGIN;
    }

    public String getDB_PASSWORD() {
        return DB_PASSWORD;
    }
}
