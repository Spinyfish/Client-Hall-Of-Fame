package com.alan.clients.security;

import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.network.Packet;
import org.atteo.classindex.IndexSubclasses;

@IndexSubclasses
public abstract class SecurityFeature implements InstanceAccess {

    private final String check, description;

    public SecurityFeature(final String check, final String description) {
        this.check = check;
        this.description = description;
    }

    public abstract boolean handle(final Packet<?> packet);
}
