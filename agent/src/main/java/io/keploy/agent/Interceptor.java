package io.keploy.agent;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.concurrent.Callable;


public class Interceptor {
    public static ResultSet executeQuery(
            @This
            Object zuper,
            @SuperCall
            Callable<?> client,
            @Origin Method method
    ) throws Exception {
        System.out.println("Here is the flow ...");

        try {
            System.out.println("Inside sql Interceptor");
            ResultSet response = (ResultSet) client.call();
            System.out.println(response);
            XStream xstream = new XStream();
            xstream.alias("sql_response", ResultSet.class);
            xstream.addPermission(AnyTypePermission.ANY);
            String xml = xstream.toXML(response);
            ResultSet res = (ResultSet) xstream.fromXML(xml);
            System.out.println("********* KSQL INTERcePtor *************");
            System.out.println(res);
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

}