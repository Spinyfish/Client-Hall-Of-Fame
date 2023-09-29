package java.virtual;

public @interface JvmSpec {

    State state() default State.MEDIUM;
    Modifier[] modifiers() default Modifier.JVM_ACTIVE;

}