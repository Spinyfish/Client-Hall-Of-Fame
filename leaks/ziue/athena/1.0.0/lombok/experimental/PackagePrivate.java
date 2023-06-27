package lombok.experimental;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface PackagePrivate {
}
