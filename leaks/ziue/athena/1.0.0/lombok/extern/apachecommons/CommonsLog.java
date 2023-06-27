package lombok.extern.apachecommons;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface CommonsLog {
    String topic() default "";
}
