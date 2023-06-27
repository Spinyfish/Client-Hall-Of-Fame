package lombok.experimental;

import java.lang.annotation.*;
import lombok.*;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface Wither {
    AccessLevel value() default AccessLevel.PUBLIC;
    
    AnyAnnotation[] onMethod() default {};
    
    AnyAnnotation[] onParam() default {};
    
    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    public @interface AnyAnnotation {
    }
}
