package lombok.experimental;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface ExtensionMethod {
    Class<?>[] value();
    
    boolean suppressBaseMethods() default true;
}
