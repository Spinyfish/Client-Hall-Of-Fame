package org.intellij.lang.annotations;

import java.lang.annotation.*;
import org.jetbrains.annotations.*;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE })
public @interface Language {
    @NonNls
    String value();
    
    @NonNls
    String prefix() default "";
    
    @NonNls
    String suffix() default "";
}
