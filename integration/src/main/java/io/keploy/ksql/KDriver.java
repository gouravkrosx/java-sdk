package io.keploy.ksql;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.postgresql.Driver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private Integer _version = 1;
    private Connection _connection;
    public Boolean _isConnected = false;

    private String _lastInsertId = "-1";

    public KDriver()  {
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

    @SneakyThrows
    @Override
    public Connection connect(String url, Properties info)  {
        System.out.println("HI THERE Mocked!");
//       PgConnection pgConnection = new PgConnection(null, info, url);
        Kcontext kctx = Context.getCtx();
        if (Objects.equals(System.getenv("KEPLOY_MODE"), "test")) {
            Connection jdbcConnection = Mockito.mock(Connection.class);
            XStream xstream = new XStream();
            xstream.alias("Connection", Connection.class);
            xstream.addPermission(AnyTypePermission.ANY);
            String xml = xstream.toXML(wrappedDriver.connect(url, info));
            Path path
                    = Paths.get("/Users/sarthak_1/Documents/Keploy/java/java-sdk/conn.txt");

            // Custom string as an input

            // Try block to check for exceptions
//            try {
//                // Now calling Files.writeString() method
//                // with path , content & standard charsets
//                Files.writeString(path, xml,
//                        StandardCharsets.UTF_8);
//            }
////
////            // Catch block to handle the exception
//            catch (IOException ex) {
//                // Print messqage exception occurred as
//                // invalid. directory local path is passed
//                System.out.print("Invalid Path");
//            }
            jdbcConnection  = (Connection) xstream.fromXML(xml);
            return new KConnection(jdbcConnection);
        }
        _connection = wrappedDriver.connect(url, info);
//        Connection kobj = new KConnection((Connection) when(_connection).thenReturn(jdbcConnection));
        Connection kobj = new KConnection(_connection);
        return kobj;
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
}
