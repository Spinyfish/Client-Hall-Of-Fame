package org.intellij.lang.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE })
@Language("RegExp")
public @interface RegExp {
    String prefix() default "";
    
    String suffix() default "";
}
