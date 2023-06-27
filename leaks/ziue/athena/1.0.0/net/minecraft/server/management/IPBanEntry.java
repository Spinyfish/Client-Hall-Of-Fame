package net.minecraft.server.management;

import java.util.*;
import com.google.gson.*;

public class IPBanEntry extends BanEntry<String>
{
    public IPBanEntry(final String p_i46330_1_) {
        this(p_i46330_1_, null, null, null, null);
    }
    
    public IPBanEntry(final String p_i1159_1_, final Date startDate, final String banner, final Date endDate, final String p_i1159_5_) {
        super(p_i1159_1_, startDate, banner, endDate, p_i1159_5_);
    }
    
    public IPBanEntry(final JsonObject p_i46331_1_) {
        super(getIPFromJson(p_i46331_1_), p_i46331_1_);
    }
    
    private static String getIPFromJson(final JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }
    
    @Override
    protected void onSerialization(final JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("ip", (String)this.getValue());
            super.onSerialization(data);
        }
    }
}
