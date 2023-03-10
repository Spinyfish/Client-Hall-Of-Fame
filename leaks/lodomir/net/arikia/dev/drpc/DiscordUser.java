/*
 * Decompiled with CFR 0.152.
 */
package net.arikia.dev.drpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class DiscordUser
extends Structure {
    public String userId;
    public String username;
    public String discriminator;
    public String avatar;

    @Override
    public List<String> getFieldOrder() {
        return Arrays.asList("userId", "username", "discriminator", "avatar");
    }
}

