package lombok;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface RequiredArgsConstructor {
    String staticName() default "";
    
    AnyAnnotation[] onConstructor() default {};
    
    AccessLevel access() default AccessLevel.PUBLIC;
    
    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    public @interface AnyAnnotation {
    }
}
