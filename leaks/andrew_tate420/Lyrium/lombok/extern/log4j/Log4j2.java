// 
// Decompiled by Procyon v0.5.36
// 

package lombok.extern.log4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Log4j2 {
    String topic() default "";
}
