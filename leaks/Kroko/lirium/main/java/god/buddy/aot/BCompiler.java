package god.buddy.aot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BCompiler
{
  AOT aot() default AOT.NONE;

  public static enum AOT
  {
    AGGRESSIVE,  NORMAL,  NONE;
  }
}