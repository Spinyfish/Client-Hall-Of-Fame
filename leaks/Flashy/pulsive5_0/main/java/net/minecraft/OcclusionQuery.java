package net.minecraft;

import java.util.UUID;

public class OcclusionQuery {
    private final UUID uuid; //Owner
    public int nextQuery;
    public boolean refresh = true;
    public boolean occluded;
    private long executionTime = 0;

    public OcclusionQuery(UUID uuid) {
        this.uuid = uuid;
    }
}