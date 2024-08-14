package ru.alexnika.faker.http.server.statistics.DBStorage;

import ru.alexnika.faker.http.server.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnectionManager {
    private static final Logger logger = LogManager.getLogger(DBConnectionManager.class.getName());
    private static final String DB_URL = Config.getProperty("db.url");
    private static final String DB_LOGIN = Config.getProperty("db.login");
    private static final String DB_PASSWORD = Config.getProperty("db.password");
    private static DBConnectionManager dbConnectionManager;

    public DBConnectionManager() {
    }

    public static DBConnectionManager getDBConnectionManager() {
        if(dbConnectionManager == null) {
            dbConnectionManager = new DBConnectionManager();
        }
        return dbConnectionManager;
    }

    public Connection getDBConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_LOGIN, DB_PASSWORD);
        } catch (SQLException  e) {
            logger.error("Can't connect to DB", e);
            return null;
        }
    }
}
