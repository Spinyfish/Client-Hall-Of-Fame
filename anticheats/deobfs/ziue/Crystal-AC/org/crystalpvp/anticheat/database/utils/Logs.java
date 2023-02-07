package org.crystalpvp.anticheat.database.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Logs {

    @Getter
    private static List<Logs> queue = new ArrayList<>();

    private UUID uuid;
    private String flag;
    private String client;
    private long ping;
    private double tps;
    private long timestamp;

}
