package de.datasecs.hydra.shared.protocol.packets.listener;

/**
 * Created with love by DataSecs on 29.09.2017.
 *
 * The HydraPacketListener interface is a wrapper for every kind of listener that is supposed to handle packet events.
 * Simply implement it, but use the {@link Handler} for methods that are supposed to handle a packet.
 * <br>
 * See <a href="https://github.com/DataSecs/Hydra/tree/master/client/src/test/java/client">the client examples</a> or
 * the <a href="https://github.com/DataSecs/Hydra/tree/master/server/src/test/java/server">the server examples</a> for
 * more accurate information on the correct usage of the PacketHandler annotation.
 */
public interface HydraPacketListener {

}