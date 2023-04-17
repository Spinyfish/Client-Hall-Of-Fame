package me.levansj01.verus.storage.database.pool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum ConnectionType {
    MYSQL("com.mysql.jdbc.Driver","com.mysql.jdbc.jdbc2.optional.MysqlDataSource","port","mysql"),
    POSTGRESQL("org.postgresql.ds.PGSimpleDataSource","org.postgresql.ds.PGSimpleDataSource","portNumber","postgresql");
    
    private final String className,
            port, 
            uri, 
            hikariCP;

    public String getUri() {
        return this.uri;
    }
}
