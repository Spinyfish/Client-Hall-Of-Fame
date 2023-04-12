package xyz.unnamed.anticheat.model.packet.nms;

import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import org.atteo.classindex.ClassIndex;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.packet.UPacket;
import xyz.unnamed.anticheat.model.packet.impl.PacketWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public class PacketLookup {
    public static Class<? extends UWrappedPacketDataSerializer> SERIALIZER_CACHE = null;
    private static final Map<Class<?>, Class<? extends UPacket>> PACKETS = new HashMap<>();

    public static UPacket getPacket(Object nmsPacket) {
        try {
            if (PACKETS.containsKey(nmsPacket.getClass()))
                return PACKETS.get(nmsPacket.getClass()).newInstance();

            Optional<Class<? extends UPacket>> wrappedClass = StreamSupport.stream(ClassIndex.getSubclasses(UPacket.class, Anticheat.class.getClassLoader()).spliterator(), false)
                    .filter(c -> c.isAnnotationPresent(PacketWrapper.class))
                    .filter(c -> c.getAnnotation(PacketWrapper.class).nmsClass() == nmsPacket.getClass())
                    .findFirst();

            if (!wrappedClass.isPresent())
                return null;

            PACKETS.put(nmsPacket.getClass(), wrappedClass.get());
            return wrappedClass.get().newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SneakyThrows
    public static UPacket readNmsPacket(Object packet) {
        if (SERIALIZER_CACHE == null)
            SERIALIZER_CACHE = UWrappedPacketDataSerializer.getVersionSerializer();

        UPacket uPacket = PacketLookup.getPacket(packet);

        if (uPacket == null)
            return null;

        UWrappedPacketDataSerializer s = SERIALIZER_CACHE.newInstance();
        s.setBuffer(Unpooled.buffer());
        s.serializePacket(packet);

        uPacket.read(s);
        return uPacket;
    }
}
