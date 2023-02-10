/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 16:03
 */
package dev.myth.api.command;

import dev.myth.api.interfaces.IMethods;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Command implements IMethods {

    private final Info info = getClass().getAnnotation(Info.class);

    @Getter private final String name = info.name();
    @Getter private final String description = info.description();

    public void run(String[] args) {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Info {
        String name();
        String description() default "";
    }
}
