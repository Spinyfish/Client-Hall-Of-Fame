package net.minecraft.command;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class CommandResultStats
{
    /** The number of result command result types that are possible. */
    private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
    private String[] field_179675_c;
    private String[] field_179673_d;

    public CommandResultStats()
    {
        this.field_179675_c = STRING_RESULT_TYPES;
        this.field_179673_d = STRING_RESULT_TYPES;
    }

    public void func_179672_a(ICommandSender sender, CommandResultStats.Type resultTypeIn, int p_179672_3_)
    {
        String var4 = this.field_179675_c[resultTypeIn.getTypeID()];

        if (var4 != null)
        {
            String var5;

            try
            {
                var5 = CommandBase.getEntityName(sender, var4);
            }
            catch (EntityNotFoundException var10)
            {
                return;
            }

            String var6 = this.field_179673_d[resultTypeIn.getTypeID()];

            if (var6 != null)
            {
                Scoreboard var7 = sender.getEntityWorld().getScoreboard();
                ScoreObjective var8 = var7.getObjective(var6);

                if (var8 != null)
                {
                    if (var7.entityHasObjective(var5, var8))
                    {
                        Score var9 = var7.getValueFromObjective(var5, var8);
                        var9.setScorePoints(p_179672_3_);
                    }
                }
            }
        }
    }

    public void readStatsFromNBT(NBTTagCompound tagcompound)
    {
        if (tagcompound.hasKey("CommandStats", 10))
        {
            NBTTagCompound var2 = tagcompound.getCompoundTag("CommandStats");
            CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                CommandResultStats.Type var6 = var3[var5];
                String var7 = var6.getTypeName() + "Name";
                String var8 = var6.getTypeName() + "Objective";

                if (var2.hasKey(var7, 8) && var2.hasKey(var8, 8))
                {
                    String var9 = var2.getString(var7);
                    String var10 = var2.getString(var8);
                    func_179667_a(this, var6, var9, var10);
                }
            }
        }
    }

    public void writeStatsToNBT(NBTTagCompound tagcompound)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            CommandResultStats.Type var6 = var3[var5];
            String var7 = this.field_179675_c[var6.getTypeID()];
            String var8 = this.field_179673_d[var6.getTypeID()];

            if (var7 != null && var8 != null)
            {
                var2.setString(var6.getTypeName() + "Name", var7);
                var2.setString(var6.getTypeName() + "Objective", var8);
            }
        }

        if (!var2.hasNoTags())
        {
            tagcompound.setTag("CommandStats", var2);
        }
    }

    public static void func_179667_a(CommandResultStats stats, CommandResultStats.Type resultType, String p_179667_2_, String p_179667_3_)
    {
        if (p_179667_2_ != null && p_179667_2_.length() != 0 && p_179667_3_ != null && p_179667_3_.length() != 0)
        {
            if (stats.field_179675_c == STRING_RESULT_TYPES || stats.field_179673_d == STRING_RESULT_TYPES)
            {
                stats.field_179675_c = new String[NUM_RESULT_TYPES];
                stats.field_179673_d = new String[NUM_RESULT_TYPES];
            }

            stats.field_179675_c[resultType.getTypeID()] = p_179667_2_;
            stats.field_179673_d[resultType.getTypeID()] = p_179667_3_;
        }
        else
        {
            func_179669_a(stats, resultType);
        }
    }

    private static void func_179669_a(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn)
    {
        if (resultStatsIn.field_179675_c != STRING_RESULT_TYPES && resultStatsIn.field_179673_d != STRING_RESULT_TYPES)
        {
            resultStatsIn.field_179675_c[resultTypeIn.getTypeID()] = null;
            resultStatsIn.field_179673_d[resultTypeIn.getTypeID()] = null;
            boolean var2 = true;
            CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                CommandResultStats.Type var6 = var3[var5];

                if (resultStatsIn.field_179675_c[var6.getTypeID()] != null && resultStatsIn.field_179673_d[var6.getTypeID()] != null)
                {
                    var2 = false;
                    break;
                }
            }

            if (var2)
            {
                resultStatsIn.field_179675_c = STRING_RESULT_TYPES;
                resultStatsIn.field_179673_d = STRING_RESULT_TYPES;
            }
        }
    }

    public void func_179671_a(CommandResultStats resultStatsIn)
    {
        CommandResultStats.Type[] var2 = CommandResultStats.Type.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            CommandResultStats.Type var5 = var2[var4];
            func_179667_a(this, var5, resultStatsIn.field_179675_c[var5.getTypeID()], resultStatsIn.field_179673_d[var5.getTypeID()]);
        }
    }

    public static enum Type
    {
        SUCCESS_COUNT("SUCCESS_COUNT", 0, 0, "SuccessCount"),
        AFFECTED_BLOCKS("AFFECTED_BLOCKS", 1, 1, "AffectedBlocks"),
        AFFECTED_ENTITIES("AFFECTED_ENTITIES", 2, 2, "AffectedEntities"),
        AFFECTED_ITEMS("AFFECTED_ITEMS", 3, 3, "AffectedItems"),
        QUERY_RESULT("QUERY_RESULT", 4, 4, "QueryResult");
        final int typeID;
        final String typeName;

        private Type(String p_i46050_1_, int p_i46050_2_, int id, String name)
        {
            this.typeID = id;
            this.typeName = name;
        }

        public int getTypeID()
        {
            return this.typeID;
        }

        public String getTypeName()
        {
            return this.typeName;
        }

        public static String[] getTypeNames()
        {
            String[] var0 = new String[values().length];
            int var1 = 0;
            CommandResultStats.Type[] var2 = values();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                CommandResultStats.Type var5 = var2[var4];
                var0[var1++] = var5.getTypeName();
            }

            return var0;
        }

        public static CommandResultStats.Type getTypeByName(String name)
        {
            CommandResultStats.Type[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3)
            {
                CommandResultStats.Type var4 = var1[var3];

                if (var4.getTypeName().equals(name))
                {
                    return var4;
                }
            }

            return null;
        }
    }
}
