package lombok;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
@Deprecated
public @interface Delegate {
    Class<?>[] types() default {};
    
    Class<?>[] excludes() default {};
}
