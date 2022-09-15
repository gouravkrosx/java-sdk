package io.keploy.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Kagent {
    public static void premain(String arguments, Instrumentation instrumentation) {

        System.out.println("Premain: Start Advice Agent to get running methods");

        new AgentBuilder.Default()
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                .type(named("com.mysql.cj.jdbc.ClientPreparedStatement"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
                    System.out.println("Inside transformer");
                    ClassFileLocator.Compound compound = new ClassFileLocator.Compound(ClassFileLocator.ForClassLoader.of(classLoader),
                            ClassFileLocator.ForClassLoader.ofSystemLoader());
                    System.out.println("Inside Sql Transformer");
                    return builder.method(named("executeQuery"))
//                                    .and(returns(named("com.mysql.cj.jdbc.result.ResultSetInternalMethods"))))
                            .intercept(MethodDelegation.to(TypePool.Default.of(compound).describe("io.keploy.agent.Interceptor").resolve()));
                }).installOn(instrumentation);
    }
}

//java -javaagent:/Users/sarthak_1/Documents/Keploy/java/java-sdk/agent/target/agent-1.0.0-SNAPSHOT.jar