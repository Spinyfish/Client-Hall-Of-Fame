package lombok.extern.log4j;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Log4j {
    String topic() default "";
}
