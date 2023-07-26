package dev.hera.client.mods;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(
        ElementType.TYPE
)
@Retention(
        RetentionPolicy.RUNTIME
)
public @interface ModInfo {
    String name();
    String description();
    Category category();
    int keyCode() default Keyboard.KEY_NONE;

}