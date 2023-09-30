/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
    private TreeMap<String, Value> theGameRules = new TreeMap();

    public GameRules() {
        this.addGameRule("doFireTick", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("mobGriefing", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("keepInventory", "false", ValueType.BOOLEAN_VALUE);
        this.addGameRule("doMobSpawning", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("doMobLoot", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("doTileDrops", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("doEntityDrops", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("commandBlockOutput", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("naturalRegeneration", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("doDaylightCycle", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("logAdminCommands", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("showDeathMessages", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("randomTickSpeed", "3", ValueType.NUMERICAL_VALUE);
        this.addGameRule("sendCommandFeedback", "true", ValueType.BOOLEAN_VALUE);
        this.addGameRule("reducedDebugInfo", "false", ValueType.BOOLEAN_VALUE);
    }

    public void addGameRule(String key, String value, ValueType type) {
        this.theGameRules.put(key, new Value(value, type));
    }

    public void setOrCreateGameRule(String key, String ruleValue) {
        Value gamerules$value = this.theGameRules.get(key);
        if (gamerules$value != null) {
            gamerules$value.setValue(ruleValue);
        } else {
            this.addGameRule(key, ruleValue, ValueType.ANY_VALUE);
        }
    }

    public String getGameRuleStringValue(String name) {
        Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null ? gamerules$value.getGameRuleStringValue() : "";
    }

    public boolean getGameRuleBooleanValue(String name) {
        Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null ? gamerules$value.getGameRuleBooleanValue() : false;
    }

    public int getInt(String name) {
        Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null ? gamerules$value.getInt() : 0;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        for (String s : this.theGameRules.keySet()) {
            Value gamerules$value = this.theGameRules.get(s);
            nbttagcompound.setString(s, gamerules$value.getGameRuleStringValue());
        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        for (String s : nbt.getKeySet()) {
            String s1 = nbt.getString(s);
            this.setOrCreateGameRule(s, s1);
        }
    }

    public String[] getRules() {
        Set<String> set = this.theGameRules.keySet();
        return set.toArray(new String[set.size()]);
    }

    public boolean hasRule(String name) {
        return this.theGameRules.containsKey(name);
    }

    public boolean areSameType(String key, ValueType otherValue) {
        Value gamerules$value = this.theGameRules.get(key);
        return gamerules$value != null && (gamerules$value.getType() == otherValue || otherValue == ValueType.ANY_VALUE);
    }

    public static enum ValueType {
        ANY_VALUE,
        BOOLEAN_VALUE,
        NUMERICAL_VALUE;

    }

    static class Value {
        private String valueString;
        private boolean valueBoolean;
        private int valueInteger;
        private double valueDouble;
        private final ValueType type;

        public Value(String value, ValueType type) {
            this.type = type;
            this.setValue(value);
        }

        public void setValue(String value) {
            this.valueString = value;
            if (value != null) {
                if (value.equals("false")) {
                    this.valueBoolean = false;
                    return;
                }
                if (value.equals("true")) {
                    this.valueBoolean = true;
                    return;
                }
            }
            this.valueBoolean = Boolean.parseBoolean(value);
            this.valueInteger = this.valueBoolean ? 1 : 0;
            try {
                this.valueInteger = Integer.parseInt(value);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            try {
                this.valueDouble = Double.parseDouble(value);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }

        public String getGameRuleStringValue() {
            return this.valueString;
        }

        public boolean getGameRuleBooleanValue() {
            return this.valueBoolean;
        }

        public int getInt() {
            return this.valueInteger;
        }

        public ValueType getType() {
            return this.type;
        }
    }
}

