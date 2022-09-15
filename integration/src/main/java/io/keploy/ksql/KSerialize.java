package io.keploy.ksql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.TimeZone;

public class KSerialize {
    private byte[] data;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    PreparedStatement kpst;
    public KSerialize(PreparedStatement kpst) {
        this.kpst = kpst;
    }

    public void getAllAccess() throws NoSuchFieldException, IllegalAccessException {
        Field privateField
                = PreparedStatement.class.getDeclaredField("defaultTimeZone");

        // Set the accessibility as true
        privateField.setAccessible(true);

        // Store the value of private field in variable
        TimeZone name = (TimeZone)privateField.get(kpst);

        // Print the value
        System.out.println("Name of TimeZone:" + name);
    }
    public void serialize(PreparedStatement ps) {
        String s1 = "sdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        String s = gson.toJson(s1);
        try {
            writer.write(s);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = outputStream.toByteArray();
    }

//    public <T> deserialize(){
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
//        InputStreamReader reader = new InputStreamReader(inputStream);
//        T kpst3 = gson.fromJson(reader, T.class);
//        return T;
//    }
}
