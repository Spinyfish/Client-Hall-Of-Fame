package me.kansio.client.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandData {
    String name();
    String description();
    String[] aliases() default {};
}
