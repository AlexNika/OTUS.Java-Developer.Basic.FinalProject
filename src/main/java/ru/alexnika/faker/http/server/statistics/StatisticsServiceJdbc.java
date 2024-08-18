package ru.alexnika.faker.http.server.statistics;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.alexnika.faker.http.server.exceptions.NoDBConfigException;
import ru.alexnika.faker.http.server.request.HttpMethod;
import ru.alexnika.faker.http.server.request.HttpProtocol;
import ru.alexnika.faker.http.server.statistics.DBStorage.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class StatisticsServiceJdbc {
    private static final Logger logger = LogManager.getLogger(StatisticsServiceJdbc.class.getName());
    private static DBConnection dbConnection;

    public static void dbInit() throws NoDBConfigException {
        dbConnection = new DBConnection();
        logger.debug("DB_URL: {}", dbConnection.getDB_URL());
        logger.debug("DB_LOGIN: {}", dbConnection.getDB_LOGIN());    }

    @Contract(pure = true)
    private static @NotNull String insertNewRecord() {
        return """
                INSERT INTO statistics (date, method, uri, protocol, remote_address, accept,
                 content_type) VALUES (?, ?, ?, ?, ?, ?, ?)""";
    }

    @Contract(pure = true)
    private static @NotNull String selectAllRecords() {
        return """
                SELECT * FROM statistics""";
    }

    @Contract(pure = true)
    private static @NotNull String selectRecordsById() {
        return """
                SELECT * FROM statistics WHERE id = ?""";
    }

    @Contract(pure = true)
    private static @NotNull String selectRecordsByMethod() {
        return """
                SELECT * FROM statistics WHERE method = ?""";
    }

    @Contract(pure = true)
    private static @NotNull String updateRecordsById() {
        return """
                 UPDATE statistics SET billed = ? WHERE id = ?""";
    }

    @Contract(pure = true)
    private static @NotNull String deleteAllRecords() {
        return """
                DELETE FROM statistics""";
    }

    public static @Nullable List<Statistics> selectAll() throws ClassNotFoundException {
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                List<Statistics> allStatistics = new ArrayList<>();
                try (PreparedStatement ps = connection.prepareStatement(selectAllRecords())) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            Timestamp date = rs.getTimestamp("date");
                            HttpMethod method = HttpMethod.valueOf(rs.getString("method"));
                            String uri = rs.getString("uri");
                            HttpProtocol protocol = HttpProtocol.valueOf(rs.getString("protocol"));
                            String remote_address = rs.getString("remote_address");
                            String accept = rs.getString("accept");
                            String contentType = rs.getString("content_type");
                            boolean billed = rs.getBoolean("billed");
                            allStatistics.add(new Statistics(id, date, method, uri, protocol, remote_address,
                                    accept, contentType, billed));
                        }
                    }
                    return allStatistics;
                }
            }
        } catch (SQLException e) {
            logger.error("Can't select * from statistics database", e);
        }
        return null;
    }

    public static @Nullable List<Statistics> selectByMethod(String requestedMethod) throws ClassNotFoundException {
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                List<Statistics> statisticsByMethod = new ArrayList<>();
                try (PreparedStatement ps = connection.prepareStatement(selectRecordsByMethod())) {
                    ps.setObject(1, requestedMethod);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            HttpMethod method = HttpMethod.valueOf(rs.getString("method"));
                            if (method.toString().equals(requestedMethod)) {
                                int id = rs.getInt("id");
                                Timestamp date = rs.getTimestamp("date");
                                String uri = rs.getString("uri");
                                HttpProtocol protocol = HttpProtocol.valueOf(rs.getString("protocol"));
                                String remote_address = rs.getString("remote_address");
                                String accept = rs.getString("accept");
                                String contentType = rs.getString("content_type");
                                boolean billed = rs.getBoolean("billed");
                                statisticsByMethod.add(new Statistics(id, date, method, uri, protocol, remote_address,
                                        accept, contentType, billed));
                            }
                        }
                    }
                    return statisticsByMethod;
                }
            }
        } catch (SQLException e) {
            logger.error("Can't select records filtered by method from statistics database", e);
        }
        return null;
    }

    public static @Nullable Statistics selectById(int requestedId) throws ClassNotFoundException {
        Statistics statisticsById = null;
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                try (PreparedStatement ps = connection.prepareStatement(selectRecordsById())) {
                    ps.setInt(1, requestedId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = Integer.parseInt(rs.getString("id"));
                            if (id == requestedId) {
                                Timestamp date = rs.getTimestamp("date");
                                HttpMethod method = HttpMethod.valueOf(rs.getString("method"));
                                String uri = rs.getString("uri");
                                HttpProtocol protocol = HttpProtocol.valueOf(rs.getString("protocol"));
                                String remote_address = rs.getString("remote_address");
                                String accept = rs.getString("accept");
                                String contentType = rs.getString("content_type");
                                boolean billed = rs.getBoolean("billed");
                                statisticsById = new Statistics(id, date, method, uri, protocol, remote_address, accept,
                                        contentType, billed);
                            }
                        }
                    }
                    return statisticsById;
                }
            }
        } catch (SQLException e) {
            logger.error("Can't select records filtered by id from statistics database", e);
        }
        return statisticsById;
    }


    public static void insert(@NotNull Statistics statistics) throws ClassNotFoundException {
        if (statistics.getUri().contains("/statistics")) {
            return;
        }
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                try (PreparedStatement ps = connection.prepareStatement(insertNewRecord())) {
                    ps.setObject(1, statistics.getDate());
                    ps.setObject(2, statistics.getMethod().toString());
                    ps.setString(3, statistics.getUri());
                    ps.setObject(4, statistics.getProtocol().toString());
                    ps.setString(5, statistics.getRemoteAddress());
                    ps.setString(6, statistics.getAccept());
                    ps.setString(7, statistics.getContentType());
                    int result = ps.executeUpdate();
                    if (result != 1) {
                        logger.error("Unexpected exception while inserting new statistics to database");
                        throw new SQLException("Unexpected exception while inserting new statistics to database");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Can't insert new statistics row into database", e);
        }
    }

    public static void update(int requestId, boolean requestBilled) throws ClassNotFoundException {
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                try (PreparedStatement ps = connection.prepareStatement(updateRecordsById())) {
                    ps.setObject(1, requestBilled);
                    ps.setObject(2, requestId);
                    int result = ps.executeUpdate();
                    if (result != 1) {
                        logger.error("Unexpected exception while updating existing statistics to database");
                        throw new SQLException("Unexpected exception while updating existing statistics to database");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Can't update statistics row from database", e);
        }
    }

    public static int deleteAll() throws ClassNotFoundException {
        int result = -1;
        try (Connection connection = makeDBConnection()) {
            if (connection != null) {
                try (PreparedStatement ps = connection.prepareStatement(deleteAllRecords())) {
                    result = ps.executeUpdate();
                    logger.debug("result of delete all statistics record: {}", result);
                    if (result == -1) {
                        logger.error("Unexpected exception while deleting all statistics from database");
                        throw new SQLException("Unexpected exception while deleting all statistics from database");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Can't delete statistics row from database", e);
        }
        return result;
    }

    private static @Nullable Connection makeDBConnection() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        try {
            return DriverManager.getConnection(dbConnection.getDB_URL(), dbConnection.getDB_LOGIN(),
                    dbConnection.getDB_PASSWORD());
        } catch (SQLException e) {
            logger.error("Can't connect to dataBase", e);
            return null;
        }
    }
}
