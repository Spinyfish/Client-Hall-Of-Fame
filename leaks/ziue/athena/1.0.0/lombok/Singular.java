package lombok;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.SOURCE)
public @interface Singular {
    String value() default "";
    
    boolean ignoreNullCollections() default false;
}
