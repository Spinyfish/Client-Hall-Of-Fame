package org.jetbrains.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface Contract {
    String value() default "";
    
    boolean pure() default false;
    
    String mutates() default "";
}
