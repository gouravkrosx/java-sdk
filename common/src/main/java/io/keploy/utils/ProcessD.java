package io.keploy.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.keploy.grpc.stubs.Service;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ProcessD {

    private static final Logger logger = LogManager.getLogger(Process.class);
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
//        System.out.println("Here is the deps !! " + deps);
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

                List<T> res = new ArrayList<>();
                for (T output : outputs) {
                    ProcessDep<T> ser = new ProcessDep<>(output);

                    byte[] bin = deps.get(0).toByteArray();
                    T obj = ser.decode(bin);
                    if (obj == null) {
                        logger.error("dependency mocking failed: failed to decode object for testID : {}", kctx.getTestId());
                        return new depsobj<>(false, null);
                    }
                    res.add(obj);

                    kctx.getDeps().remove(0);
                    return new depsobj<>(true, res);
                }

                if (kctx.getFileExport()) {
                    logger.info("Returned the mocked outputs for Generic dependency call with meta: {}", meta);
                }
                kctx.getMock().remove(0);
                return new depsobj<>(true, res);

            case MODE_RECORD:
                Service.Dependency.Builder Dependencies = Service.Dependency.newBuilder();
                List<Service.DataBytes> dblist = new ArrayList<>(); //this is 2d array
                Map<String, String> metas = new HashMap<>();
                for (T output : outputs) {
                    ProcessDep<T> ser = new ProcessDep<>(output);
                    binResult = ser.encoded(output);
                    metas = ser.getMeta();
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

//                System.out.println(d.getBin());


                Dependencies.setName(metas.get("name")).setType(metas.get("type")).putAllMeta(metas);


        }

        return new depsobj<>(false, null);
    }

    public static byte[] convertBytes(ArrayList<Byte> binResult)
    {
        byte[] ret = new byte[binResult.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = binResult.get(i);
        }
        return ret;
    }


}