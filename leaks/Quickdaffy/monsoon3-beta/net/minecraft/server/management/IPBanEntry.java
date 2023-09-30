/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraft.server.management.BanEntry;

public class IPBanEntry
extends BanEntry<String> {
    public IPBanEntry(String p_i46330_1_) {
        this(p_i46330_1_, (Date)null, (String)null, (Date)null, (String)null);
    }

    public IPBanEntry(String p_i1159_1_, Date startDate, String banner, Date endDate, String p_i1159_5_) {
        super(p_i1159_1_, startDate, banner, endDate, p_i1159_5_);
    }

    public IPBanEntry(JsonObject p_i46331_1_) {
        super(IPBanEntry.getIPFromJson(p_i46331_1_), p_i46331_1_);
    }

    private static String getIPFromJson(JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }

    @Override
    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("ip", (String)this.getValue());
            super.onSerialization(data);
        }
    }
}

