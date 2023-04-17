package me.levansj01.verus.check.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.Map;

public enum CheckType {
    AIM_ASSIST("AimAssist", "1"), AUTO_CLICKER("AutoClicker", "2"), BAD_PACKETS("BadPackets", "3"), FLY("Fly", "4"),
    INVENTORY("Inventory", "5"), KILL_AURA("KillAura", "6"), PAYLOAD("Payload", "7"), PHASE("Phase", "8"),
    REACH("Reach", "9"), SPEED("Speed", "10"), TIMER("Timer", "11"), VELOCITY("Velocity", "12"), MISC("Misc", "13"),
    SCAFFOLD("Scaffold", "14"), SERVER_CRASHER("Server Crasher", "15"), MANUAL("Manual", "100");

    private final String name;

    private static final Map<String, CheckType> nameMap;

    private final String suffix;

    public String getName() {
        return this.name;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public CheckType next() {
        int n = this.ordinal() + 1;
        if (this.ordinal() + 1 > 14) {
            n = 14;
        } else if (n == 7) {
            n = 8;
        }
        return CheckType.values()[n];
    }

    static {
        ImmutableMap.Builder<String, CheckType> builder = new Builder<String, CheckType>();
        for (CheckType checkType : CheckType.values()) {
            builder.put(checkType.name.toLowerCase(),checkType);
        }
        nameMap = builder.build();
    }

    public CheckType previous() {
        int n = this.ordinal() - 1;
        if (n < 0) {
            n = 0;
        } else if (n == 7) {
            n = 6;
        }
        return CheckType.values()[n];
    }

    public boolean ignore() {
        return this.equals(PHASE) || this.equals(MANUAL);
    }

    public boolean valid() {
        return !ignore();
    }

    public static CheckType getByString(String string) {
        return (CheckType) (nameMap.get(string));
    }

    private CheckType(String name, String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

}