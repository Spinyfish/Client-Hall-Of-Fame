package me.jinthium.straight.impl.settings.mode;

import jdk.jfr.Registered;
import me.jinthium.straight.api.module.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModeInfo {

    String name();
    Class<? extends Module> parent();
}
