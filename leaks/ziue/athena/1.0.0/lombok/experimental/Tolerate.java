package lombok.experimental;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.SOURCE)
public @interface Tolerate {
}
