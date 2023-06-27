package org.jetbrains.annotations;

import java.lang.annotation.*;

public interface Async
{
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    public @interface Execute {
    }
    
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    public @interface Schedule {
    }
}
