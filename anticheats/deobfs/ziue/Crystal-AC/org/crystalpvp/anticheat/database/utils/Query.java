package org.crystalpvp.anticheat.database.utils;

import lombok.SneakyThrows;

import java.sql.Connection;

public class Query {
    private static Connection conn;

    public static void use(Connection conn) {
        Query.conn = conn;
    }

    @SneakyThrows
    public static ExecutableStatement prepare(@Language("MySQL") String query) {
        return new ExecutableStatement(conn.prepareStatement(query));
    }

    @SneakyThrows
    public static ExecutableStatement prepare(@Language("MySQL") String query, Connection con) {
        return new ExecutableStatement(con.prepareStatement(query));
    }
}
