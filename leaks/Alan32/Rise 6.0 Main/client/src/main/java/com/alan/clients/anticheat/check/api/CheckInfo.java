package com.alan.clients.anticheat.check.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();

    String type();

    String description();

    boolean dev() default false;
}
