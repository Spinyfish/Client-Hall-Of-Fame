package lombok.experimental;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface SuperBuilder {
    String builderMethodName() default "builder";
    
    String buildMethodName() default "build";
    
    boolean toBuilder() default false;
    
    String setterPrefix() default "";
}
