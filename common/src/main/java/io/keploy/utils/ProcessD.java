package io.keploy.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import io.keploy.grpc.stubs.Service;
import io.keploy.regression.context.Context;
import io.keploy.regression.context.Kcontext;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ProcessD {

    private static final Logger logger = LogManager.getLogger(Process.class);
    public static ArrayList<ArrayList<Byte>> binResult = new ArrayList<>();

    @SafeVarargs
    public static <T> depsobj ProcessDep(Map<String, String> meta, T... outputs) throws InvalidProtocolBufferException {

        Kcontext kctx = Context.getCtx();
        if (kctx == null) {
            logger.error("dependency mocking failed: failed to get Keploy context");
            return new depsobj<String>(false, null);
        }
        Service.Dependency dependency = kctx.getDeps().get(0);
        switch (kctx.getMode()) {
            case MODE_TEST:
                List<Service.Dependency> deps = kctx.getDeps();

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

                    byte[] bin = dependency.getDataList().get(0).getBin().toByteArray();
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
                for (T output : outputs) {
                    ProcessDep<T> ser = new ProcessDep<>(output);
                    binResult = ser.encode(output);
                    if (binResult == null) {
                        logger.error("dependency capture failed: failed to encode object test id : {}", kctx.getTestId());
                        return new depsobj<>(false, null);
                    }
                }
                Service.Dependency.Builder Dependencies = Service.Dependency.newBuilder();

                Dependencies.setName(meta.get("name")).setType(meta.get("type")).putAllMeta(meta);

                for (int i = 0; i < binResult.size(); i++) {
                    // ab yha pe ek ek karke values set kardenge poore 2d array ki deps k
                    byte[] data = convertBytes(binResult.get(i));
                    Dependencies.setData(i, Service.DataBytes.parseFrom(data));
                }
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