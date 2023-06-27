package lombok.extern.jbosslog;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface JBossLog {
    String topic() default "";
}
