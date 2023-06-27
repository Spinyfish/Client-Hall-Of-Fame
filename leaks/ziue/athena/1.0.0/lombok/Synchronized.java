package lombok;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface Synchronized {
    String value() default "";
}
