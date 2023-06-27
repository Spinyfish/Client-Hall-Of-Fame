package lombok.experimental;

import java.lang.annotation.*;
import lombok.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface StandardException {
    AccessLevel access() default AccessLevel.PUBLIC;
}
