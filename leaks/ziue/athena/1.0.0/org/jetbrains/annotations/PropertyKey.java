package org.jetbrains.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD })
public @interface PropertyKey {
    String resourceBundle();
}
