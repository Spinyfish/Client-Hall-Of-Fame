package me.levansj01.verus.api.manager;

import lombok.Getter;

import java.util.UUID;

public class DataNotFoundException extends Exception {

    @Getter private final UUID uuid;

    public DataNotFoundException(UUID uuid) {
        super("Could not find data of " + uuid.toString());
        this.uuid = uuid;
    }

}