package lombok;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface NoArgsConstructor {
    String staticName() default "";
    
    AnyAnnotation[] onConstructor() default {};
    
    AccessLevel access() default AccessLevel.PUBLIC;
    
    boolean force() default false;
    
    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    public @interface AnyAnnotation {
    }
}
