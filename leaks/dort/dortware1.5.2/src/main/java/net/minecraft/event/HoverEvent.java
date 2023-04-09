package net.minecraft.event;

import com.google.common.collect.Maps;
import net.minecraft.util.IChatComponent;

import java.util.Map;

public class HoverEvent {
    private final HoverEvent.Action action;
    private final IChatComponent value;
    // private static final String __OBFID = "CL_00001264";

    public HoverEvent(HoverEvent.Action p_i45158_1_, IChatComponent p_i45158_2_) {
        this.action = p_i45158_1_;
        this.value = p_i45158_2_;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public HoverEvent.Action getAction() {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "show item",
     * this would be the item to show.
     */
    public IChatComponent getValue() {
        return this.value;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            HoverEvent var2 = (HoverEvent) p_equals_1_;

            if (this.action != var2.action) {
                return false;
            } else {
                if (this.value != null) {
                    return this.value.equals(var2.value);
                } else return var2.value == null;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int var1 = this.action.hashCode();
        var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
        return var1;
    }

    public enum Action {
        SHOW_TEXT("show_text", true),
        SHOW_ACHIEVEMENT("show_achievement", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);
        private static final Map nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        // private static final String __OBFID = "CL_00001265";

        Action(String p_i45157_3_, boolean p_i45157_4_) {
            this.canonicalName = p_i45157_3_;
            this.allowedInChat = p_i45157_4_;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static HoverEvent.Action getValueByCanonicalName(String p_150684_0_) {
            return (HoverEvent.Action) nameMapping.get(p_150684_0_);
        }

        static {
            HoverEvent.Action[] var0 = values();

            for (Action var3 : var0) {
                nameMapping.put(var3.getCanonicalName(), var3);
            }
        }
    }
}
