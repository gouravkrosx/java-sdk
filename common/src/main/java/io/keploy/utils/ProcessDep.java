package io.keploy.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProcessDep<T> {


    ArrayList<ArrayList<Byte>> arrLL = new ArrayList<>();
    T obj;
    String xml = "";
    XStream xstream = null;
    public ProcessDep(T obj) {
        this.obj = obj;
    }

    public T getObject() {
        return this.obj;
    }

    public byte[] encoded(T output) {
        xstream = new XStream();
        xstream.alias("T", output.getClass());
        xstream.addPermission(AnyTypePermission.ANY);
        xml = xstream.toXML(getObject());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        try {
            writer.write(xml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

    public T decode(byte[] bin) {
        System.out.println("THIS IS BYTE ARRAY !! ! ! " + Arrays.toString(bin));
        String xml2 = new String(bin, StandardCharsets.UTF_8);
//        System.out.println("str1 >> " + xml2);
        return (T) xstream.fromXML(xml2);
    }
    public static void  call(){

    }
//    private depsobj<T> processDeps(T obj) throws InvalidProtocolBufferException {
//       // meta fill here
////        depsobj var = ProcessD.ProcessDep(meta, obj);
////        return var;
//    }

    public Map<String, String> getMeta() {
        Map<String, String> meta= new HashMap<>();
        meta.put("name", "SQL");
        meta.put("type", "SQL_DB");
        meta.put("operation", "executeQuery");
        return meta;
    }
}
