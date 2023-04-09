package net.minecraft.event;

import com.google.common.collect.Maps;

import java.util.Map;

public class ClickEvent {
    private final ClickEvent.Action action;
    private final String value;
    // private static final String __OBFID = "CL_00001260";

    public ClickEvent(ClickEvent.Action p_i45156_1_, String p_i45156_2_) {
        this.action = p_i45156_1_;
        this.value = p_i45156_2_;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public ClickEvent.Action getAction() {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "open URL",
     * this would be the URL to open.
     */
    public String getValue() {
        return this.value;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ClickEvent var2 = (ClickEvent) p_equals_1_;

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
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int var1 = this.action.hashCode();
        var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
        return var1;
    }

    public enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        TWITCH_USER_INFO("twitch_user_info", false),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true);
        private static final Map nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        // private static final String __OBFID = "CL_00001261";

        Action(String p_i45155_3_, boolean p_i45155_4_) {
            this.canonicalName = p_i45155_3_;
            this.allowedInChat = p_i45155_4_;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static ClickEvent.Action getValueByCanonicalName(String p_150672_0_) {
            return (ClickEvent.Action) nameMapping.get(p_150672_0_);
        }

        static {
            ClickEvent.Action[] var0 = values();

            for (Action var3 : var0) {
                nameMapping.put(var3.getCanonicalName(), var3);
            }
        }
    }
}
