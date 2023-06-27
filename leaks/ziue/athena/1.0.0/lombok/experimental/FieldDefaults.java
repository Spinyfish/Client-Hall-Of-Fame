package lombok.experimental;

import java.lang.annotation.*;
import lombok.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface FieldDefaults {
    AccessLevel level() default AccessLevel.NONE;
    
    boolean makeFinal() default false;
}
