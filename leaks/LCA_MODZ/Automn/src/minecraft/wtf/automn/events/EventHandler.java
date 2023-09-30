package wtf.automn.events;


import wtf.automn.events.types.Priority;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

  byte value() default Priority.MEDIUM;
}
