package org.jetbrains.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE, ElementType.PACKAGE })
public @interface Nls {
    Capitalization capitalization() default Capitalization.NotSpecified;
    
    public enum Capitalization
    {
        NotSpecified, 
        Title, 
        Sentence;
    }
}
