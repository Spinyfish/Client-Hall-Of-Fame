package net.minecraft.command;

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
    /**
     * The number of result command result types that are possible.
     */
    private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
    private String[] field_179675_c;
    private String[] field_179673_d;

    public CommandResultStats() {
        this.field_179675_c = STRING_RESULT_TYPES;
        this.field_179673_d = STRING_RESULT_TYPES;
    }

    public void func_179672_a(final ICommandSender sender, final CommandResultStats.Type resultTypeIn, final int p_179672_3_) {
        final String s = this.field_179675_c[resultTypeIn.getTypeID()];

        if (s != null) {
            final ICommandSender icommandsender = new ICommandSender() {
                public String getCommandSenderName() {
                    return sender.getCommandSenderName();
                }

                public IChatComponent getDisplayName() {
                    return sender.getDisplayName();
                }

                public void addChatMessage(final IChatComponent component) {
                    sender.addChatMessage(component);
                }

                public boolean canCommandSenderUseCommand(final int permLevel, final String commandName) {
                    return true;
                }

                public BlockPos getPosition() {
                    return sender.getPosition();
                }

                public Vec3 getPositionVector() {
                    return sender.getPositionVector();
                }

                public World getEntityWorld() {
                    return sender.getEntityWorld();
                }

                public Entity getCommandSenderEntity() {
                    return sender.getCommandSenderEntity();
                }

                public boolean sendCommandFeedback() {
                    return sender.sendCommandFeedback();
                }

                public void setCommandStat(final CommandResultStats.Type type, final int amount) {
                    sender.setCommandStat(type, amount);
                }
            };
            final String s1;

            try {
                s1 = CommandBase.getEntityName(icommandsender, s);
            } catch (final EntityNotFoundException var11) {
                return;
            }

            final String s2 = this.field_179673_d[resultTypeIn.getTypeID()];

            if (s2 != null) {
                final Scoreboard scoreboard = sender.getEntityWorld().getScoreboard();
                final ScoreObjective scoreobjective = scoreboard.getObjective(s2);

                if (scoreobjective != null) {
                    if (scoreboard.entityHasObjective(s1, scoreobjective)) {
                        final Score score = scoreboard.getValueFromObjective(s1, scoreobjective);
                        score.setScorePoints(p_179672_3_);
                    }
                }
            }
        }
    }

    public void readStatsFromNBT(final NBTTagCompound tagcompound) {
        if (tagcompound.hasKey("CommandStats", 10)) {
            final NBTTagCompound nbttagcompound = tagcompound.getCompoundTag("CommandStats");

            for (final CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values()) {
                final String s = commandresultstats$type.getTypeName() + "Name";
                final String s1 = commandresultstats$type.getTypeName() + "Objective";

                if (nbttagcompound.hasKey(s, 8) && nbttagcompound.hasKey(s1, 8)) {
                    final String s2 = nbttagcompound.getString(s);
                    final String s3 = nbttagcompound.getString(s1);
                    func_179667_a(this, commandresultstats$type, s2, s3);
                }
            }
        }
    }

    public void writeStatsToNBT(final NBTTagCompound tagcompound) {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (final CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values()) {
            final String s = this.field_179675_c[commandresultstats$type.getTypeID()];
            final String s1 = this.field_179673_d[commandresultstats$type.getTypeID()];

            if (s != null && s1 != null) {
                nbttagcompound.setString(commandresultstats$type.getTypeName() + "Name", s);
                nbttagcompound.setString(commandresultstats$type.getTypeName() + "Objective", s1);
            }
        }

        if (!nbttagcompound.hasNoTags()) {
            tagcompound.setTag("CommandStats", nbttagcompound);
        }
    }

    public static void func_179667_a(final CommandResultStats stats, final CommandResultStats.Type resultType, final String p_179667_2_, final String p_179667_3_) {
        if (p_179667_2_ != null && p_179667_2_.length() != 0 && p_179667_3_ != null && p_179667_3_.length() != 0) {
            if (stats.field_179675_c == STRING_RESULT_TYPES || stats.field_179673_d == STRING_RESULT_TYPES) {
                stats.field_179675_c = new String[NUM_RESULT_TYPES];
                stats.field_179673_d = new String[NUM_RESULT_TYPES];
            }

            stats.field_179675_c[resultType.getTypeID()] = p_179667_2_;
            stats.field_179673_d[resultType.getTypeID()] = p_179667_3_;
        } else {
            func_179669_a(stats, resultType);
        }
    }

    private static void func_179669_a(final CommandResultStats resultStatsIn, final CommandResultStats.Type resultTypeIn) {
        if (resultStatsIn.field_179675_c != STRING_RESULT_TYPES && resultStatsIn.field_179673_d != STRING_RESULT_TYPES) {
            resultStatsIn.field_179675_c[resultTypeIn.getTypeID()] = null;
            resultStatsIn.field_179673_d[resultTypeIn.getTypeID()] = null;
            boolean flag = true;

            for (final CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values()) {
                if (resultStatsIn.field_179675_c[commandresultstats$type.getTypeID()] != null && resultStatsIn.field_179673_d[commandresultstats$type.getTypeID()] != null) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                resultStatsIn.field_179675_c = STRING_RESULT_TYPES;
                resultStatsIn.field_179673_d = STRING_RESULT_TYPES;
            }
        }
    }

    public void func_179671_a(final CommandResultStats resultStatsIn) {
        for (final CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values()) {
            func_179667_a(this, commandresultstats$type, resultStatsIn.field_179675_c[commandresultstats$type.getTypeID()], resultStatsIn.field_179673_d[commandresultstats$type.getTypeID()]);
        }
    }

    public enum Type {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        final int typeID;
        final String typeName;

        Type(final int id, final String name) {
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
            final String[] astring = new String[values().length];
            int i = 0;

            for (final CommandResultStats.Type commandresultstats$type : values()) {
                astring[i++] = commandresultstats$type.getTypeName();
            }

            return astring;
        }

        public static CommandResultStats.Type getTypeByName(final String name) {
            for (final CommandResultStats.Type commandresultstats$type : values()) {
                if (commandresultstats$type.getTypeName().equals(name)) {
                    return commandresultstats$type;
                }
            }

            return null;
        }
    }
}
