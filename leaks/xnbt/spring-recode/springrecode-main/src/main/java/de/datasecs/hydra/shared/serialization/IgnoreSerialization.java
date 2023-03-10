package de.datasecs.hydra.shared.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with love by DataSec on 29.09.2017.
 *
 * The IgnoreSerialization annotation is specifically intended and used for setter methods in a custom class.
 * The annotation acts like the keyword 'transient' for fields in classes that implement the serializable class.
 * <br>
 * For an example of how to work with the ignore serialization annotation, visit the
 * <a href="https://github.com/DataSecs/Hydra/tree/master/client/src/test/java/client/serialization">client custom class serialization example</a>
 * or the <a href="https://github.com/DataSecs/Hydra/tree/master/server/src/test/java/server/serialization">server custom class serialization example</a>.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreSerialization {

}