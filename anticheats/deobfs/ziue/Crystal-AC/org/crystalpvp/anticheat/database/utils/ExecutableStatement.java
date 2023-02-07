package org.crystalpvp.anticheat.database.utils;

import org.crystalpvp.anticheat.api.util.DBUtil;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.UUID;

public class ExecutableStatement {
    private PreparedStatement statement;
    private int pos = 1;

    public ExecutableStatement(PreparedStatement statement) {
        this.statement = statement;
    }

    @SneakyThrows
    public Integer execute() {
        try {
            return statement.executeUpdate();
        } finally {
            DBUtil.close(statement);
        }
    }

    @SneakyThrows
    public void execute(ResultSetIterator iterator) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
            while (rs.next()) iterator.next(rs);
        } finally {
            DBUtil.close(statement, rs);
        }
    }

    @SneakyThrows
    public void executeSingle(ResultSetIterator iterator) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
            if (rs.next()) iterator.next(rs);
            else iterator.next(null);
        } finally {
            DBUtil.close(statement, rs);
        }
    }

    @SneakyThrows
    public ResultSet executeQuery() {
        return statement.executeQuery();
    }

    @SneakyThrows
    public ExecutableStatement append(Object obj) {
        statement.setObject(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(String obj) {
        statement.setString(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(UUID uuid) {
        if (uuid != null) statement.setString(pos++, uuid.toString().replace("-", ""));
        else statement.setString(pos++, null);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Array obj) {
        statement.setArray(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Integer obj) {
        statement.setInt(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Short obj) {
        statement.setShort(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Long obj) {
        statement.setLong(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Float obj) {
        statement.setFloat(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Double obj) {
        statement.setDouble(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Date obj) {
        statement.setDate(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Timestamp obj) {
        statement.setTimestamp(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Time obj) {
        statement.setTime(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(Blob obj) {
        statement.setBlob(pos++, obj);
        return this;
    }

    @SneakyThrows
    public ExecutableStatement append(byte[] obj) {
        statement.setBytes(pos++, obj);
        return this;
    }
}
