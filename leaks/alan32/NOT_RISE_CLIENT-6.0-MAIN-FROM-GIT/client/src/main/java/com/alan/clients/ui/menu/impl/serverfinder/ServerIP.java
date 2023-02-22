package com.alan.clients.ui.menu.impl.serverfinder;

import lombok.Getter;

/**
 * @author Auth
 * @since 10/07/2022
 */

@Getter
public class ServerIP { // TODO: make not shit make int[] parts

    private final int first;
    private final int second;
    private final int third;
    private final int fourth;
    private final String[] split;

    public int getPart(final int part) {
        return Integer.parseInt(split[part]);
    }

    public void setPart(final int part, final int value) {
        split[part] = String.valueOf(value);
    }

    public ServerIP(final int first, final int second, final int third, final int fourth) {
        split = new String[]{String.valueOf(first), String.valueOf(second), String.valueOf(third), String.valueOf(fourth)};
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public ServerIP(final String ip) {
        split = ip.split("\\.");
        this.first = Integer.parseInt(split[0]);
        this.second = Integer.parseInt(split[1]);
        this.third = Integer.parseInt(split[2]);
        this.fourth = Integer.parseInt(split[3]);
    }

    public static ServerIP min(final ServerIP first, final ServerIP second) {
        final int minFirst = Math.min(first.first, second.first);
        final int minSecond = Math.min(first.second, second.second);
        final int minThird = Math.min(first.third, second.third);
        final int minFourth = Math.min(first.fourth, second.fourth);
        return new ServerIP(minFirst, minSecond, minThird, minFourth);
    }

    public static ServerIP max(final ServerIP first, final ServerIP second) {
        final int maxFirst = Math.max(first.first, second.first);
        final int maxSecond = Math.max(first.second, second.second);
        final int maxThird = Math.max(first.third, second.third);
        final int maxFourth = Math.max(first.fourth, second.fourth);
        return new ServerIP(maxFirst, maxSecond, maxThird, maxFourth);
    }

    public String toString() {
        return split[0] + "." + split[1] + "." + split[2] + "." + split[3];
    }
}
