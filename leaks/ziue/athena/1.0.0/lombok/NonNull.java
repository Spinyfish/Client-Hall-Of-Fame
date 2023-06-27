package lombok;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE })
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface NonNull {
}
