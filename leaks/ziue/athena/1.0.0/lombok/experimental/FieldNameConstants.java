package lombok.experimental;

import java.lang.annotation.*;
import lombok.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface FieldNameConstants {
    AccessLevel level() default AccessLevel.PUBLIC;
    
    boolean asEnum() default false;
    
    String innerTypeName() default "";
    
    boolean onlyExplicitlyIncluded() default false;
    
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Exclude {
    }
    
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Include {
    }
}
