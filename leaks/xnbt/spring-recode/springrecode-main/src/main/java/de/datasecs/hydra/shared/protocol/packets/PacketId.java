package de.datasecs.hydra.shared.protocol.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with love by DataSecs on 29.09.2017.
 *
 * The PacketId annotation is used to mark packets with an id in the protocol of Hydra.
 * By giving the packet an id, it is possible to distinguish the packets when serializing and deserializing them.
 * <br>
 * For an example of how to work with the packet id annotation, visit the
 * <a href="https://github.com/DataSecs/Hydra/tree/master/client/src/test/java/client">client example</a>
 * or the <a href="https://github.com/DataSecs/Hydra/tree/master/server/src/test/java/server">server example</a>.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketId {

    byte value() default 0;
}