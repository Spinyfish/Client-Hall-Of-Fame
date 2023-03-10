package de.datasecs.hydra.shared.protocol.packets.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with love by DataSecs on 01.10.2017.
 *
 * The PacketHandler annotation is used for methods in packet listener classes, that are supposed to handle packets.
 * Simply add the annotation to the method that is supposed to handle a specific packet.
 * But be aware of the notation the methods must have.
 * <br>
 * For an example of how to work with the packet handler annotation, visit the
 * <a href="https://github.com/DataSecs/Hydra/tree/master/client/src/test/java/client">client example</a>
 * or the <a href="https://github.com/DataSecs/Hydra/tree/master/server/src/test/java/server">server example</a>.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketHandler {

}