package org.intellij.lang.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface Flow {
    public static final String DEFAULT_SOURCE = "The method argument (if parameter was annotated) or this container (if instance method was annotated)";
    public static final String THIS_SOURCE = "this";
    public static final String DEFAULT_TARGET = "This container (if the parameter was annotated) or the return value (if instance method was annotated)";
    public static final String RETURN_METHOD_TARGET = "The return value of this method";
    public static final String THIS_TARGET = "this";
    
    String source() default "The method argument (if parameter was annotated) or this container (if instance method was annotated)";
    
    boolean sourceIsContainer() default false;
    
    String target() default "This container (if the parameter was annotated) or the return value (if instance method was annotated)";
    
    boolean targetIsContainer() default false;
}
