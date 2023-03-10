/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TabCompleteStorage
implements StorableObject {
    private final Map<UUID, String> usernames = new HashMap<UUID, String>();
    private final Set<String> commands = new HashSet<String>();
    private int lastId;
    private String lastRequest;
    private boolean lastAssumeCommand;

    public Map<UUID, String> usernames() {
        return this.usernames;
    }

    public Set<String> commands() {
        return this.commands;
    }

    public int lastId() {
        return this.lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public String lastRequest() {
        return this.lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public boolean isLastAssumeCommand() {
        return this.lastAssumeCommand;
    }

    public void setLastAssumeCommand(boolean lastAssumeCommand) {
        this.lastAssumeCommand = lastAssumeCommand;
    }
}

