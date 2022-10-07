package io.keploy.agent;

import io.keploy.regression.mode;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class Kagent {
    private static void setMode(){
        if (mode.getMode() == null) {
            String envMode = "record";
            if (System.getenv("KEPLOY_MODE") != null) {
                envMode = System.getenv("KEPLOY_MODE");
            }

            switch (envMode) {
                case "record":
                    new mode().setMode(mode.ModeType.MODE_RECORD);
                    break;
                case "test":
                    new mode().setMode(mode.ModeType.MODE_TEST);
                    break;
                case "off":
                    new mode().setMode(mode.ModeType.MODE_OFF);
                    break;
            }
        }
    }
    public static void premain(String arguments, Instrumentation instrumentation) {
        System.out.println(System.getenv("KEPLOY_MODE"));
//        setMode();
        if (Objects.equals(System.getenv("KEPLOY_MODE"), "off")){
            return;
        }
        System.out.println("Premain: Start Advice Agent to get running methods");

        new AgentBuilder.Default()
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
//                .type(named("io.keploy.ksql.KPreparedStatement"))
//                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
//                    System.out.println("Inside transformer");
//                    ClassFileLocator.Compound compound = new ClassFileLocator.Compound(ClassFileLocator.ForClassLoader.of(classLoader),
//                            ClassFileLocator.ForClassLoader.ofSystemLoader());
//                    System.out.println("Inside Sql Transformer");
//                    return builder.method(named("executeQuery"))
////                                    .and(returns(named("com.mysql.cj.jdbc.result.ResultSetInternalMethods"))))
//                            .intercept(MethodDelegation.to(TypePool.Default.of(compound).describe("io.keploy.agent.Interceptor").resolve()));
//                })
                .type(named("org.springframework.boot.autoconfigure.jdbc.DataSourceProperties"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {

                    System.out.println("Inside RegisterDriverAdvice1 Transformer");
                    return builder.method(named("setDriverClassName"))
                            .intercept(Advice.to(TypePool.Default.ofSystemLoader().describe("io.keploy.agent.RegisterDriverAdvice").resolve(), ClassFileLocator.ForClassLoader.ofSystemLoader()));
                })
                .type(named("org.springframework.boot.autoconfigure.jdbc.DataSourceProperties"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {

                    System.out.println("Inside RegisterDriverAdvice2 Transformer");
                    return builder.method(named("determineDriverClassName"))
                            .intercept(MethodDelegation.to(TypePool.Default.ofSystemLoader().describe("io.keploy.agent.RegisterDriverAdvice_Interceptor").resolve()));
                })
                .type(named("org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
                    System.out.println("Inside HibernateProperties Transformer");
                    return builder.method(named("setDdlAuto").and(takesArgument(0, String.class))).intercept(Advice.to(TypePool.Default.ofSystemLoader().describe("io.keploy.agent.SetDdlAuto_Advice").resolve(), ClassFileLocator.ForClassLoader.ofSystemLoader()));
                })
                .installOn(instrumentation);


//        new AgentBuilder.Default()
//                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
//                .type(named("org.springframework.boot.autoconfigure.jdbc.DataSourceProperties"))
//                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
//
//                    System.out.println("Inside DatasourceProperties Transformer");
//                    return builder.method(named("setDriverClassName"))
//                            .intercept(Advice.to(TypePool.Default.ofSystemLoader().describe("io.keploy.agent.DataSourceAdvice").resolve(), ClassFileLocator.ForClassLoader.ofSystemLoader()));
//                }).installOn(instrumentation);

    }
}

//java -javaagent:/Users/sarthak_1/Documents/Keploy/java/java-sdk/agent/target/agent-1.0.0-SNAPSHOT.jar