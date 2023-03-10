/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
    private static final int NUM_RESULT_TYPES = Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
    private String[] field_179675_c = STRING_RESULT_TYPES;
    private String[] field_179673_d = STRING_RESULT_TYPES;

    public void func_179672_a(final ICommandSender sender, Type resultTypeIn, int p_179672_3_) {
        String s = this.field_179675_c[resultTypeIn.getTypeID()];
        if (s != null) {
            Scoreboard scoreboard;
            ScoreObjective scoreobjective;
            String s1;
            ICommandSender icommandsender = new ICommandSender(){

                @Override
                public String getName() {
                    return sender.getName();
                }

                @Override
                public IChatComponent getDisplayName() {
                    return sender.getDisplayName();
                }

                @Override
                public void addChatMessage(IChatComponent component) {
                    sender.addChatMessage(component);
                }

                @Override
                public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
                    return true;
                }

                @Override
                public BlockPos getPosition() {
                    return sender.getPosition();
                }

                @Override
                public Vec3 getPositionVector() {
                    return sender.getPositionVector();
                }

                @Override
                public World getEntityWorld() {
                    return sender.getEntityWorld();
                }

                @Override
                public Entity getCommandSenderEntity() {
                    return sender.getCommandSenderEntity();
                }

                @Override
                public boolean sendCommandFeedback() {
                    return sender.sendCommandFeedback();
                }

                @Override
                public void setCommandStat(Type type, int amount) {
                    sender.setCommandStat(type, amount);
                }
            };
            try {
                s1 = CommandBase.getEntityName(icommandsender, s);
            }
            catch (EntityNotFoundException var11) {
                return;
            }
            String s2 = this.field_179673_d[resultTypeIn.getTypeID()];
            if (s2 != null && (scoreobjective = (scoreboard = sender.getEntityWorld().getScoreboard()).getObjective(s2)) != null && scoreboard.entityHasObjective(s1, scoreobjective)) {
                Score score = scoreboard.getValueFromObjective(s1, scoreobjective);
                score.setScorePoints(p_179672_3_);
            }
        }
    }

    public void readStatsFromNBT(NBTTagCompound tagcompound) {
        if (tagcompound.hasKey("CommandStats", 10)) {
            NBTTagCompound nbttagcompound = tagcompound.getCompoundTag("CommandStats");
            for (Type commandresultstats$type : Type.values()) {
                String s = commandresultstats$type.getTypeName() + "Name";
                String s1 = commandresultstats$type.getTypeName() + "Objective";
                if (!nbttagcompound.hasKey(s, 8) || !nbttagcompound.hasKey(s1, 8)) continue;
                String s2 = nbttagcompound.getString(s);
                String s3 = nbttagcompound.getString(s1);
                CommandResultStats.func_179667_a(this, commandresultstats$type, s2, s3);
            }
        }
    }

    public void writeStatsToNBT(NBTTagCompound tagcompound) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        for (Type commandresultstats$type : Type.values()) {
            String s = this.field_179675_c[commandresultstats$type.getTypeID()];
            String s1 = this.field_179673_d[commandresultstats$type.getTypeID()];
            if (s == null || s1 == null) continue;
            nbttagcompound.setString(commandresultstats$type.getTypeName() + "Name", s);
            nbttagcompound.setString(commandresultstats$type.getTypeName() + "Objective", s1);
        }
        if (!nbttagcompound.hasNoTags()) {
            tagcompound.setTag("CommandStats", nbttagcompound);
        }
    }

    public static void func_179667_a(CommandResultStats stats, Type resultType, String p_179667_2_, String p_179667_3_) {
        if (p_179667_2_ != null && p_179667_2_.length() != 0 && p_179667_3_ != null && p_179667_3_.length() != 0) {
            if (stats.field_179675_c == STRING_RESULT_TYPES || stats.field_179673_d == STRING_RESULT_TYPES) {
                stats.field_179675_c = new String[NUM_RESULT_TYPES];
                stats.field_179673_d = new String[NUM_RESULT_TYPES];
            }
            stats.field_179675_c[resultType.getTypeID()] = p_179667_2_;
            stats.field_179673_d[resultType.getTypeID()] = p_179667_3_;
        } else {
            CommandResultStats.func_179669_a(stats, resultType);
        }
    }

    private static void func_179669_a(CommandResultStats resultStatsIn, Type resultTypeIn) {
        if (resultStatsIn.field_179675_c != STRING_RESULT_TYPES && resultStatsIn.field_179673_d != STRING_RESULT_TYPES) {
            resultStatsIn.field_179675_c[resultTypeIn.getTypeID()] = null;
            resultStatsIn.field_179673_d[resultTypeIn.getTypeID()] = null;
            boolean flag = true;
            for (Type commandresultstats$type : Type.values()) {
                if (resultStatsIn.field_179675_c[commandresultstats$type.getTypeID()] == null || resultStatsIn.field_179673_d[commandresultstats$type.getTypeID()] == null) continue;
                flag = false;
                break;
            }
            if (flag) {
                resultStatsIn.field_179675_c = STRING_RESULT_TYPES;
                resultStatsIn.field_179673_d = STRING_RESULT_TYPES;
            }
        }
    }

    public void func_179671_a(CommandResultStats resultStatsIn) {
        for (Type commandresultstats$type : Type.values()) {
            CommandResultStats.func_179667_a(this, commandresultstats$type, resultStatsIn.field_179675_c[commandresultstats$type.getTypeID()], resultStatsIn.field_179673_d[commandresultstats$type.getTypeID()]);
        }
    }

    public static enum Type {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        final int typeID;
        final String typeName;

        private Type(int id, String name) {
            this.typeID = id;
            this.typeName = name;
        }

        public int getTypeID() {
            return this.typeID;
        }

        public String getTypeName() {
            return this.typeName;
        }

        public static String[] getTypeNames() {
            String[] astring = new String[Type.values().length];
            int i = 0;
            for (Type commandresultstats$type : Type.values()) {
                astring[i++] = commandresultstats$type.getTypeName();
            }
            return astring;
        }

        public static Type getTypeByName(String name) {
            for (Type commandresultstats$type : Type.values()) {
                if (!commandresultstats$type.getTypeName().equals(name)) continue;
                return commandresultstats$type;
            }
            return null;
        }
    }
}

