package io.keploy.utils;

import com.google.common.primitives.Bytes;
import com.google.protobuf.InvalidProtocolBufferException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProcessDep<T> {
    private byte[] data;

    Map<String, String> meta= new HashMap<>();
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

    public ArrayList<ArrayList<Byte>> encode(T output) {
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

        this.data = outputStream.toByteArray();
        ArrayList<Byte> byteList = (ArrayList<Byte>) Bytes.asList(this.data);
        arrLL.add(byteList);
        return arrLL;
    }

    public T decode(byte[] bin) {
        System.out.println("THIS IS BYTE ARRAY !! ! ! " + Arrays.toString(bin));
        String xml2 = new String(bin, StandardCharsets.UTF_8);
//        System.out.println("str1 >> " + xml2);
        return (T) xstream.fromXML(xml2);
    }
    public static void  call(){

    }
    private depsobj<T> processDeps(T obj) throws InvalidProtocolBufferException {
       // meta fill here
        depsobj var = ProcessD.ProcessDep(meta, obj);
        return var;
    }

    public Map<String, String> getMeta() {
        meta.put("name", "SQL");
        meta.put("type", "SQL_DB");
        meta.put("operation", "executeQuery");
        return meta;
    }
}
