package lombok;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.SOURCE)
public @interface SneakyThrows {
    Class<? extends Throwable>[] value() default { Throwable.class };
}
