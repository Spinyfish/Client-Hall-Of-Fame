package lombok.extern.java;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Log {
    String topic() default "";
}
