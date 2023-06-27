package lombok.extern.slf4j;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Slf4j {
    String topic() default "";
}
