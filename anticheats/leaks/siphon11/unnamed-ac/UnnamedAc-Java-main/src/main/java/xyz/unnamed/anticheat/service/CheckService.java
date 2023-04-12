package xyz.unnamed.anticheat.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.atteo.classindex.ClassIndex;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.check.AbstractCheck;
import xyz.unnamed.anticheat.model.check.PacketCheck;
import xyz.unnamed.anticheat.model.packet.UPacket;
import xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound.PacketInTransaction;
import xyz.unnamed.anticheat.model.packet.transaction.TransactionInformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@RequiredArgsConstructor
public class CheckService {
    public static final Map<Class<? extends UPacket>, List<Method>> PACKET_CHECKS = new HashMap<>();

    static {
        for (Class<? extends AbstractCheck> c : ClassIndex.getSubclasses(AbstractCheck.class, Anticheat.class.getClassLoader())) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.isAnnotationPresent(PacketCheck.class))
                    continue;

                Class<?> paramClass = m.getParameterTypes()[0];
                for (Class<? extends UPacket> packet : ClassIndex.getSubclasses(UPacket.class, Anticheat.class.getClassLoader())) {
                    if (paramClass.isAssignableFrom(packet))
                        PACKET_CHECKS.computeIfAbsent(packet, k -> new ArrayList<>()).add(m);
                }
            }
        }
    }

    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private final PlayerData playerData;
    private final LinkedList<TransactionInformation> transactionQueue = new LinkedList<>();

    private void handleTransaction() {
        TransactionInformation head = transactionQueue.peekFirst();

        if (head == null)
            return; // kick player?

        switch (head.getStatus()) {
            case TICK_START:
                head.setStatus(TransactionInformation.TransactionTickStatus.TICK_END);
                break;

            case TICK_END:
                head.getOutboundPackets().forEach(p -> p.handle(playerData));
                transactionQueue.removeFirst();
                break;
        }
    }

    private void runChecks(UPacket packet) {
        List<Method> methods = PACKET_CHECKS.get(packet.getClass());

        if (methods == null)
            return;

        methods.forEach(m -> {
            try {
                m.invoke(playerData.getChecks().get(m.getDeclaringClass()), packet);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleIncoming(UPacket packet) {
        if (packet instanceof PacketInTransaction) {
            handleTransaction();
            return;
        }

        packet.handle(playerData);
        runChecks(packet);
    }

    public void handleOutgoing(UPacket packet) {
        TransactionInformation head = transactionQueue.peek();

        if (head != null)
            head.getOutboundPackets().add(packet);
    }
}
