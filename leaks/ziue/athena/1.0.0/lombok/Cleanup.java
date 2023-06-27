package lombok;

import java.lang.annotation.*;

@Target({ ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.SOURCE)
public @interface Cleanup {
    String value() default "close";
}
