package bg.sofia.uni.fmi.mjt.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private final HikariDataSource ds;

    public DataSource(String propertiesFile) {
        HikariConfig config = new HikariConfig(propertiesFile);
        this.ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}


