package io.keploy.ksql;

import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import io.keploy.regression.mode;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.postgresql.Driver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class KDriver implements java.sql.Driver {
    public Driver wrappedDriver;

    private String _url;
    private String _username;
    private String _password;

    private String _databaseName;
    public final Kcontext kctx = Context.getCtx();
    io.keploy.regression.mode.ModeType mode = null;

    private Integer _version = 1;
    private Connection _connection;
    public Boolean _isConnected = false;

    private String _lastInsertId = "-1";

    public KDriver()  {
        if (Objects.equals(System.getenv("KEPLOY_MODE"), "record")){
            mode = io.keploy.regression.mode.ModeType.MODE_RECORD;
        }
        else if (Objects.equals(System.getenv("KEPLOY_MODE"), "test")){
            mode = io.keploy.regression.mode.ModeType.MODE_TEST;
        }
        wrappedDriver = new Driver();
        System.out.println("hello inside no-arg constructor");
    }

    private Driver getWrappedDriver() throws SQLException {
        String driver = "";
        switch (driver) {
            case "postgres":
                return new org.postgresql.Driver();
            case "mysql":
//                return new com.mysql.cj.jdbc.Driver();
            default:
                return null;
        }
    }


    @Override
    public Connection connect(String url, Properties info) throws SQLException {

        if (Objects.equals(System.getenv("KEPLOY_MODE"), "test")){
            Connection conn = new KConnection();
            MockConnection(conn);
            return conn;
        }
        Connection resultSet = null;
        try {
            resultSet = wrappedDriver.connect(url, info);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new KConnection(resultSet);

    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        boolean acceptsURL = wrappedDriver.acceptsURL(url);
        return acceptsURL;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        int getMajor = wrappedDriver.getMajorVersion();
        return getMajor;
    }


    @Override
    public int getMinorVersion() {
        int getMinor = wrappedDriver.getMinorVersion();
        return getMinor;
    }

    @Override
    public boolean jdbcCompliant() {
        boolean jdbcCompliant = wrappedDriver.jdbcCompliant();
        return jdbcCompliant;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return wrappedDriver.getParentLogger();
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private void MockConnection(Connection conn) throws SQLException {
//
//        conn.setReadOnly(true);
//        conn.setClientInfo(null);
//        conn.prepareStatement("");
//        conn.prepareStatement(null);
//        conn.prepareStatement(null);
//        conn.setClientInfo(null);
//        conn.setClientInfo(null);
//        conn.commit();
//        conn.nativeSQL("");
//        conn.getSchema();
//        conn.isValid(5);
//        conn.setNetworkTimeout(null,5);
//        conn.rollback();
//        conn.getAutoCommit();
//        conn.isClosed();
//        conn.isReadOnly();
    }
}
