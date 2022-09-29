package io.keploy.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.keploy.grpc.stubs.Service;
import io.keploy.regression.Mock;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@NoArgsConstructor
public class ProcessD {

    private static final Logger logger = LogManager.getLogger(ProcessD.class);
    //    public static ArrayList<Byte> binResult;
    public static byte[] binResult;


    @SafeVarargs
    public static <T> depsobj ProcessDep(Map<String, String> meta, T... outputs) throws InvalidProtocolBufferException {

        Kcontext kctx = Context.getCtx();
        if (kctx == null) {
            logger.error("dependency mocking failed: failed to get Keploy context");
            return new depsobj<String>(false, null);
        }
        List<Service.Dependency> deps = kctx.getDeps();
        switch (kctx.getMode()) {
            case MODE_TEST:
                if (deps == null || deps.size() == 0) {
                    logger.error("dependency mocking failed: incorrect number of dependencies in keploy context with test id: " + kctx.getTestId());
                    return new depsobj<>(false, null);
                }
                if (deps.get(0).getDataList().size() != outputs.length) {
                    logger.error("dependency mocking failed: incorrect number of dependencies in keploy context with test id: " + kctx.getTestId());
                    return new depsobj<>(false, null);
                }
                List<Object> res = new ArrayList<>();
                System.out.println(outputs.length + " length of output" + Arrays.toString(outputs));

                for (T output : outputs) {
                    List<Service.DataBytes> bin = deps.get(0).getDataList();
                    Service.DataBytes c = bin.get(0);
                    binResult = c.getBin().toByteArray();
//                    T obj = decode(binResult, output);
                    String objectClass = output.getClass().getName();
                    Object obj = null;
                    switch (objectClass) {
                        case "io.keploy.ksql.KPreparedStatement":
                            obj = decodePreparedStatement(binResult);
                            break;
                        case "org.postgresql.jdbc.PgResultSet":
                        case "io.keploy.ksql.KResultSet":
                            obj = decodeResultSet(binResult);
                            break;
                        case "io.keploy.ksql.KConnection":
                            obj = decodeConnection(binResult);
                            break;
//                        case "java.lang.Boolean":
//                            obj = decode(binResult);
//                            break;
                        default:

                    }

                    if (obj == null) {
                        logger.error("dependency mocking failed: failed to decode object for testID : {}", kctx.getTestId());
                        return new depsobj<>(false, null);
                    }
                    res.add(obj);
                }
                kctx.getDeps().remove(0);

//                if (kctx.getFileExport()) {
//                    logger.info("Returned the mocked outputs for Generic dependency call with meta: {}", meta);
//                }
//                kctx.getMock().remove(0);
                return new depsobj<>(true, res);

            case MODE_RECORD:
                Service.Dependency.Builder Dependencies = Service.Dependency.newBuilder();
                List<Service.DataBytes> dblist = new ArrayList<>(); //this is 2d array
                Map<String, String> metas = new HashMap<>();
                for (T output : outputs) {
//                    binResult = encoded(output);
                    String objectClass = output.getClass().getName();
                    System.out.println(objectClass);
                    switch (objectClass) {
                        case "org.postgresql.jdbc.PgPreparedStatement":
                        case "io.keploy.ksql.KPreparedStatement":
                            binResult = encodedPreparedStatement((PreparedStatement) output);
                            break;
                        case "org.postgresql.jdbc.PgResultSet":
                        case "io.keploy.ksql.KResultSet":
                            binResult = encodedResultSet((ResultSet) output);
                            break;
                        case "org.postgresql.jdbc.PgConnection":
                        case "io.keploy.ksql.KConnection":
                            binResult = encodedConnection((Connection) output);
                            break;
//                        case "java.lang.Boolean":
//                            binResult = encoded((boolean)output);
//                            break;
                        default:
                    }
                    metas = getMeta(output);
                    if (binResult == null) {
                        logger.error("dependency capture failed: failed to encode object test id : {}", kctx.getTestId());
                        return new depsobj<>(false, null);
                    }
                    Service.DataBytes dbytes = Service.DataBytes.newBuilder().setBin(ByteString.copyFrom(binResult)).build();
                    dblist.add(dbytes);
                }
                Service.Dependency genericDeps = Dependencies.addAllData(dblist).build();

                kctx.getDeps().add(genericDeps);
                Service.DataBytes d = Dependencies.getDataList().get(0);

                Dependencies.setName(metas.get("name")).setType(metas.get("type")).putAllMeta(metas);

                List<Service.Mock.Object> lobj = new ArrayList<>();

                Service.Mock.Object.newBuilder().setType("").build();

                for (Service.DataBytes s : dblist) {
                    Service.Mock.Object obj = Service.Mock.Object.newBuilder().setData(s.getBin()).build();
                    lobj.add(obj);
                }

                Service.Mock.SpecSchema specSchema = Service.Mock.SpecSchema.newBuilder().putAllMetadata(meta).addAllObjects(lobj).build();

                Service.Mock mock = Service.Mock.newBuilder()
                        .setVersion(Mock.Version.V1_BETA1.value)
                        .setName("")
                        .setKind(Mock.Kind.GENERIC_EXPORT.value)
                        .setSpec(specSchema)
                        .build();

                kctx.getMock().add(mock);

        }
        return new depsobj<>(false, null);
    }

    public static byte[] encoded(boolean output) {
        XStream xstream = new XStream();
        xstream.alias("boolean", boolean.class);
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.ignoreUnknownElements();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        xstream.toXML(output, writer);
        return outputStream.toByteArray();
    }
    public static boolean decode(byte[] bin) {

        ByteArrayInputStream input = new ByteArrayInputStream(bin);
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        boolean object = false;
        try {
            object = (boolean) xstream.fromXML(input);
            input.close();

        } catch (Exception e) {
            System.out.println("Exception while decoding ..... " + e);
        }
        return object;
    }

    public static <T> Map<String, String> getMeta(T obj) {
        Map<String, String> meta = new HashMap<>();
        meta.put("name", "SQL");
        meta.put("type", "SQL_DB");
        meta.put("operation", "executeQuery");
        return meta;
    }

    public static PreparedStatement decodePreparedStatement(byte[] bin) {

        ByteArrayInputStream input = new ByteArrayInputStream(bin);
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        PreparedStatement object = null;
        try {
            object = (PreparedStatement) xstream.fromXML(input);
            input.close();

        } catch (Exception e) {
            System.out.println("Exception while decoding ..... " + e);
        }
        return object;
    }

    public static byte[] encodedPreparedStatement(PreparedStatement output) {
        XStream xstream = new XStream();
        xstream.alias("PreparedStatement", PreparedStatement.class);
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.ignoreUnknownElements();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        xstream.toXML(output, writer);
        return outputStream.toByteArray();
    }

    public static Connection decodeConnection(byte[] bin) {

        ByteArrayInputStream input = new ByteArrayInputStream(bin);
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        Connection object = null;
        try {
            object = (Connection) xstream.fromXML(input);
            input.close();

        } catch (Exception e) {
            System.out.println("Exception while decoding ..... " + e);
        }
        return object;
    }

    public static byte[] encodedConnection(Connection output) {
        XStream xstream = new XStream();
        xstream.alias("Connection", Connection.class);
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.ignoreUnknownElements();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        xstream.toXML(output, writer);
        return outputStream.toByteArray();
    }

    public static byte[] encodedResultSet(ResultSet output) {
        XStream xstream = new XStream();
        xstream.alias("ResultSet", ResultSet.class);
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.ignoreUnknownElements();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        xstream.toXML(output, writer);
        return outputStream.toByteArray();
    }

    public static ResultSet decodeResultSet(byte[] bin) {

        ByteArrayInputStream input = new ByteArrayInputStream(bin);
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        ResultSet object = null;
        try {
            object = (ResultSet) xstream.fromXML(input);
            input.close();

        } catch (Exception e) {
            System.out.println("Exception while decoding ..... " + e);
        }
        return object;
    }

}