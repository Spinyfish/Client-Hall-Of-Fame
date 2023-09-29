package com.alan.clients.anticheat.check.manager;

import com.alan.clients.anticheat.check.Check;
import com.alan.clients.anticheat.check.impl.combat.VelocityCancel;
import com.alan.clients.anticheat.check.impl.movement.FlightPrediction;
import com.alan.clients.anticheat.check.impl.movement.SpeedLimit;
import com.alan.clients.anticheat.data.PlayerData;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[]{
            SpeedLimit.class,
            FlightPrediction.class,

            VelocityCancel.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();

        for (final Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }

        return checkList;
    }

    public static void setup() {
        for (final Class<?> clazz : CHECKS) {
            try {
                CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
