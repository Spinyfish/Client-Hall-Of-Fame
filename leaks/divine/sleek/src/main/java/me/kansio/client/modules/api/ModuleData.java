package me.kansio.client.modules.api;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Key;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData {

    String name();
    ModuleCategory category();

    String description();

    int bind() default Keyboard.KEY_NONE;



}
