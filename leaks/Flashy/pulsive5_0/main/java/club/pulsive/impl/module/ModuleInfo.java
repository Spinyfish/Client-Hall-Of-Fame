package club.pulsive.impl.module;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ModuleInfo {

    /*
        The name of the module.
     */
    String name();

    /*
        The name rendered on the arraylist.
     */
    String renderName() default "";

    /*
        The description of the module.
    */
    String description() default "";

    /*
        Aliases for module reference in commands.
     */
    String[] aliases() default "";

    /*
        Category of the module.
     */
    Category category();

    /*
        The keybind of the module.
    */
    int keybind() default 0;

}
