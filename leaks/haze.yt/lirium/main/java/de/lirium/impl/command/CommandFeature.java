package de.lirium.impl.command;

import de.lirium.base.event.EventListener;
import de.lirium.base.feature.Feature;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Getter
@Setter
public abstract class CommandFeature extends EventListener implements Feature {
    private final String name;
    private final String[] alias;

    public CommandFeature() {
        final Info info = getClass().getAnnotation(Info.class);
        this.name = info.name();
        this.alias = info.alias();
    }

    public String[] getArguments() {
        return null;
    }

    public abstract boolean execute(String[] args);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();

        String[] alias() default "";
    }
}