package io.keploy.ksql;

//import org.postgresql.jdbc.PgConnection;

import com.google.protobuf.InvalidProtocolBufferException;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import io.keploy.regression.mode;
import io.keploy.utils.ProcessD;
import io.keploy.utils.ProcessDep;
import io.keploy.utils.depsobj;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class KConnection implements Connection {

    private final Connection wrappedCon;

    public KConnection(Connection pgConnection) throws SQLException {
        this.wrappedCon = pgConnection;
        System.out.println("hooks connection !!");
    }


    @Override
    public Statement createStatement() throws SQLException {
        System.out.println("Mocked create Statement ! in connection ");
        Statement st = wrappedCon.createStatement();
        Statement kst = new KStatement(st);
        return kst;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs = wrappedCon.prepareStatement(sql);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }
        return new KPreparedStatement(rs);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        CallableStatement prepareCall = wrappedCon.prepareCall(sql);
        return new KCallableStatement(prepareCall);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        String nativeSQL = wrappedCon.nativeSQL(sql);
        return nativeSQL;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        wrappedCon.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        boolean getAutoCommit = wrappedCon.getAutoCommit();
        return getAutoCommit;
    }

    @Override
    public void commit() throws SQLException {
        wrappedCon.commit();
    }

    @Override
    public void rollback() throws SQLException {
        wrappedCon.rollback();
    }

    @Override
    public void close() throws SQLException {
        wrappedCon.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return wrappedCon.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData getMetaData = wrappedCon.getMetaData();
        return wrappedCon.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        wrappedCon.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return wrappedCon.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        wrappedCon.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return wrappedCon.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        wrappedCon.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return wrappedCon.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return wrappedCon.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        wrappedCon.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new KStatement(wrappedCon.createStatement(resultSetType, resultSetConcurrency));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs = wrappedCon.prepareStatement(sql, resultSetType, resultSetConcurrency);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }

        return new KPreparedStatement(rs);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new KCallableStatement(wrappedCon.prepareCall(sql, resultSetType, resultSetConcurrency));
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return wrappedCon.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        wrappedCon.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        wrappedCon.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return wrappedCon.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return new KSavepoint(wrappedCon.setSavepoint());
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return new KSavepoint(wrappedCon.setSavepoint(name));
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        wrappedCon.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        wrappedCon.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new KStatement(wrappedCon.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs= wrappedCon.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }
        return new KPreparedStatement(rs);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new KCallableStatement(wrappedCon.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs= wrappedCon.prepareStatement(sql, autoGeneratedKeys);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }
        return new KPreparedStatement(rs);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs= wrappedCon.prepareStatement(sql, columnIndexes);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }
        return new KPreparedStatement(rs);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        System.out.println("INSIDE PREPARED STATEMENT of connection !! " + sql);
        Kcontext kctx = Context.getCtx();
        mode.ModeType mode = kctx.getMode();

        System.out.println("INSIDE prepareStatement !@@!!! ");
        PreparedStatement rs = null;
        switch (mode) {
            case MODE_TEST:
                // don't run
                break;
            case MODE_RECORD:
                rs= wrappedCon.prepareStatement(sql, columnNames);
                break;
            default:
                System.out.println("integrations: Not in a valid sdk mode");
        }
        ProcessDep<PreparedStatement> resultSetProcessDep = new ProcessDep<>(rs);
        Map<String, String> meta = resultSetProcessDep.getMeta();
        depsobj rs2;
        try {
            rs2 = ProcessD.ProcessDep(meta, rs);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        if (rs2.isMock() && rs2.getRes() != null) {
            rs = (PreparedStatement) rs2.getRes();
            System.out.println("HOGYAAaaaaaaaaaa .........");
        }
        return new KPreparedStatement(rs);

    }

    @Override
    public Clob createClob() throws SQLException {
        return wrappedCon.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return wrappedCon.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return wrappedCon.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return wrappedCon.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return wrappedCon.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        wrappedCon.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        wrappedCon.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return wrappedCon.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return wrappedCon.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return wrappedCon.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return wrappedCon.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        wrappedCon.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return wrappedCon.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        wrappedCon.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        wrappedCon.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return wrappedCon.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return wrappedCon.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return wrappedCon.isWrapperFor(iface);
    }
}
