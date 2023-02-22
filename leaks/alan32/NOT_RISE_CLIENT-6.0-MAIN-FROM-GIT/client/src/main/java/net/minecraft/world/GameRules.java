package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;
import java.util.TreeMap;

public class GameRules {
    private final TreeMap<String, GameRules.Value> theGameRules = new TreeMap();

    public GameRules() {
        this.addGameRule("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
        this.addGameRule("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.addGameRule("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
    }

    public void addGameRule(final String key, final String value, final GameRules.ValueType type) {
        this.theGameRules.put(key, new GameRules.Value(value, type));
    }

    public void setOrCreateGameRule(final String key, final String ruleValue) {
        final GameRules.Value gamerules$value = this.theGameRules.get(key);

        if (gamerules$value != null) {
            gamerules$value.setValue(ruleValue);
        } else {
            this.addGameRule(key, ruleValue, GameRules.ValueType.ANY_VALUE);
        }
    }

    /**
     * Gets the string Game Rule value.
     */
    public String getGameRuleStringValue(final String name) {
        final GameRules.Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null ? gamerules$value.getGameRuleStringValue() : "";
    }

    /**
     * Gets the boolean Game Rule value.
     */
    public boolean getGameRuleBooleanValue(final String name) {
        final GameRules.Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null && gamerules$value.getGameRuleBooleanValue();
    }

    public int getInt(final String name) {
        final GameRules.Value gamerules$value = this.theGameRules.get(name);
        return gamerules$value != null ? gamerules$value.getInt() : 0;
    }

    /**
     * Return the defined game rules as NBT.
     */
    public NBTTagCompound writeToNBT() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (final String s : this.theGameRules.keySet()) {
            final GameRules.Value gamerules$value = this.theGameRules.get(s);
            nbttagcompound.setString(s, gamerules$value.getGameRuleStringValue());
        }

        return nbttagcompound;
    }

    /**
     * Set defined game rules from NBT.
     */
    public void readFromNBT(final NBTTagCompound nbt) {
        for (final String s : nbt.getKeySet()) {
            final String s1 = nbt.getString(s);
            this.setOrCreateGameRule(s, s1);
        }
    }

    /**
     * Return the defined game rules.
     */
    public String[] getRules() {
        final Set<String> set = this.theGameRules.keySet();
        return set.toArray(new String[set.size()]);
    }

    /**
     * Return whether the specified game rule is defined.
     */
    public boolean hasRule(final String name) {
        return this.theGameRules.containsKey(name);
    }

    public boolean areSameType(final String key, final GameRules.ValueType otherValue) {
        final GameRules.Value gamerules$value = this.theGameRules.get(key);
        return gamerules$value != null && (gamerules$value.getType() == otherValue || otherValue == GameRules.ValueType.ANY_VALUE);
    }

    static class Value {
        private String valueString;
        private boolean valueBoolean;
        private int valueInteger;
        private double valueDouble;
        private final GameRules.ValueType type;

        public Value(final String value, final GameRules.ValueType type) {
            this.type = type;
            this.setValue(value);
        }

        public void setValue(final String value) {
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
            } catch (final NumberFormatException var4) {
            }

            try {
                this.valueDouble = Double.parseDouble(value);
            } catch (final NumberFormatException var3) {
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

        public GameRules.ValueType getType() {
            return this.type;
        }
    }

    public enum ValueType {
        ANY_VALUE,
        BOOLEAN_VALUE,
        NUMERICAL_VALUE
    }
}
