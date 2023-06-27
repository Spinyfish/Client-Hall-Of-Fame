package lombok.extern.flogger;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Flogger {
}
