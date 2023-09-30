package dev.africa.pandaware.api.command.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInformation {
    String name();

    String description() default "Default command description";

    String[] aliases() default {};
}
