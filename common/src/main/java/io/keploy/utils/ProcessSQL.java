package io.keploy.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.keploy.grpc.stubs.Service;
import io.keploy.regression.Mock;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor

public class ProcessSQL {

    private static final Logger logger = LogManager.getLogger(ProcessSQL.class);

//    @SafeVarargs
    public static <T> depsobj ProcessDep(Map<String, String> meta, Service.Table table) throws InvalidProtocolBufferException {

        Kcontext kctx = Context.getCtx();
        if (kctx == null) {
            logger.error("dependency mocking failed: failed to get Keploy context");
            return new depsobj<String>(false, null);
        }
        List<Service.Dependency> deps = kctx.getDeps();
        switch (kctx.getMode()) {
            case MODE_TEST:
//                if (deps == null || deps.size() == 0) {
//                    logger.error("dependency mocking failed: incorrect number of dependencies in keploy context with test id: " + kctx.getTestId());
//                    return new depsobj<>(false, null);
//                }
//                if (deps.get(0).getDataList().size() != outputs.length) {
//                    logger.error("dependency mocking failed: incorrect number of dependencies in keploy context with test id: " + kctx.getTestId());
//                    return new depsobj<>(false, null);
//                }
//                List<Object> res = new ArrayList<>();
//
//
//                if (kctx.getFileExport()) {
//                    logger.info("Returned the mocked outputs for Generic dependency call with meta: {}", meta);
//                }
//                kctx.getMock().remove(0);
//                return new depsobj<>(true, res);

            case MODE_RECORD:


                Service.Mock.SpecSchema specSchema = Service.Mock.SpecSchema.newBuilder().putAllMetadata(meta).setTable(table).setType("TABLE").build();

                Service.Mock mock = Service.Mock.newBuilder()
                        .setVersion(Mock.Version.V1_BETA1.value)
                        .setName("")
                        .setKind(Mock.Kind.SQL.value)
                        .setSpec(specSchema)
                        .build();

                kctx.getMock().add(mock);

        }
        return new depsobj<>(false, null);
    }
}
