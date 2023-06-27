package lombok;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface EqualsAndHashCode {
    String[] exclude() default {};
    
    String[] of() default {};
    
    boolean callSuper() default false;
    
    boolean doNotUseGetters() default false;
    
    CacheStrategy cacheStrategy() default CacheStrategy.NEVER;
    
    AnyAnnotation[] onParam() default {};
    
    boolean onlyExplicitlyIncluded() default false;
    
    public enum CacheStrategy
    {
        NEVER("NEVER", 0), 
        LAZY("LAZY", 1);
        
        private CacheStrategy(final String s, final int n) {
        }
    }
    
    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    public @interface AnyAnnotation {
    }
    
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Exclude {
    }
    
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Include {
        String replaces() default "";
        
        int rank() default 0;
    }
}
