package lombok.experimental;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface Accessors {
    boolean fluent() default false;
    
    boolean chain() default false;
    
    boolean makeFinal() default false;
    
    String[] prefix() default {};
}
