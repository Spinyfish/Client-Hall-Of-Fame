package org.crystalpvp.anticheat.database.utils;

import java.sql.ResultSet;

public interface ResultSetIterator {
    void next(ResultSet rs) throws Exception;
}
