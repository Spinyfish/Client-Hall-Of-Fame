/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IntegerCache;
import optifine.Config;
import optifine.CustomColors;

public class PotionHelper {
    public static final String field_77924_a = null;
    public static final String sugarEffect = "-0+1-2-3&4-4+13";
    public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
    public static final String spiderEyeEffect = "-0-1+2-3&4-4+13";
    public static final String fermentedSpiderEyeEffect = "-0+3-4+13";
    public static final String speckledMelonEffect = "+0-1+2-3&4-4+13";
    public static final String blazePowderEffect = "+0-1-2+3&4-4+13";
    public static final String magmaCreamEffect = "+0+1-2-3&4-4+13";
    public static final String redstoneEffect = "-5+6-7";
    public static final String glowstoneEffect = "+5-6-7";
    public static final String gunpowderEffect = "+14&13-13";
    public static final String goldenCarrotEffect = "-0+1+2-3+13&4-4";
    public static final String pufferfishEffect = "+0-1+2+3+13&4-4";
    public static final String rabbitFootEffect = "+0+1-2+3&4-4+13";
    private static final Map potionRequirements = Maps.newHashMap();
    private static final Map potionAmplifiers = Maps.newHashMap();
    private static final Map DATAVALUE_COLORS = Maps.newHashMap();
    private static final String[] potionPrefixes = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};
    private static final String __OBFID = "CL_00000078";

    public static boolean checkFlag(int p_77914_0_, int p_77914_1_) {
        return (p_77914_0_ & 1 << p_77914_1_) != 0;
    }

    private static int isFlagSet(int p_77910_0_, int p_77910_1_) {
        return PotionHelper.checkFlag(p_77910_0_, p_77910_1_) ? 1 : 0;
    }

    private static int isFlagUnset(int p_77916_0_, int p_77916_1_) {
        return PotionHelper.checkFlag(p_77916_0_, p_77916_1_) ? 0 : 1;
    }

    public static int getPotionPrefixIndex(int dataValue) {
        return PotionHelper.func_77908_a(dataValue, 5, 4, 3, 2, 1);
    }

    public static int calcPotionLiquidColor(Collection p_77911_0_) {
        int i = 3694022;
        if (p_77911_0_ != null && !p_77911_0_.isEmpty()) {
            float f = 0.0f;
            float f1 = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            for (Object potioneffect0 : p_77911_0_) {
                PotionEffect potioneffect = (PotionEffect)potioneffect0;
                if (!potioneffect.getIsShowParticles()) continue;
                int j = Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor();
                if (Config.isCustomColors()) {
                    j = CustomColors.getPotionColor(potioneffect.getPotionID(), j);
                }
                for (int k = 0; k <= potioneffect.getAmplifier(); ++k) {
                    f += (float)(j >> 16 & 0xFF) / 255.0f;
                    f1 += (float)(j >> 8 & 0xFF) / 255.0f;
                    f2 += (float)(j >> 0 & 0xFF) / 255.0f;
                    f3 += 1.0f;
                }
            }
            if (f3 == 0.0f) {
                return 0;
            }
            f = f / f3 * 255.0f;
            f1 = f1 / f3 * 255.0f;
            f2 = f2 / f3 * 255.0f;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
        }
        if (Config.isCustomColors()) {
            i = CustomColors.getPotionColor(0, i);
        }
        return i;
    }

    public static boolean getAreAmbient(Collection potionEffects) {
        for (Object potioneffect : potionEffects) {
            if (((PotionEffect)potioneffect).getIsAmbient()) continue;
            return false;
        }
        return true;
    }

    public static int getLiquidColor(int dataValue, boolean bypassCache) {
        Integer integer = IntegerCache.func_181756_a(dataValue);
        if (!bypassCache) {
            if (DATAVALUE_COLORS.containsKey(integer)) {
                return (Integer)DATAVALUE_COLORS.get(integer);
            }
            int i = PotionHelper.calcPotionLiquidColor(PotionHelper.getPotionEffects(integer, false));
            DATAVALUE_COLORS.put(integer, i);
            return i;
        }
        return PotionHelper.calcPotionLiquidColor(PotionHelper.getPotionEffects(integer, true));
    }

    public static String getPotionPrefix(int dataValue) {
        int i = PotionHelper.getPotionPrefixIndex(dataValue);
        return potionPrefixes[i];
    }

    private static int func_77904_a(boolean p_77904_0_, boolean p_77904_1_, boolean p_77904_2_, int p_77904_3_, int p_77904_4_, int p_77904_5_, int p_77904_6_) {
        int i = 0;
        if (p_77904_0_) {
            i = PotionHelper.isFlagUnset(p_77904_6_, p_77904_4_);
        } else if (p_77904_3_ != -1) {
            if (p_77904_3_ == 0 && PotionHelper.countSetFlags(p_77904_6_) == p_77904_4_) {
                i = 1;
            } else if (p_77904_3_ == 1 && PotionHelper.countSetFlags(p_77904_6_) > p_77904_4_) {
                i = 1;
            } else if (p_77904_3_ == 2 && PotionHelper.countSetFlags(p_77904_6_) < p_77904_4_) {
                i = 1;
            }
        } else {
            i = PotionHelper.isFlagSet(p_77904_6_, p_77904_4_);
        }
        if (p_77904_1_) {
            i *= p_77904_5_;
        }
        if (p_77904_2_) {
            i *= -1;
        }
        return i;
    }

    private static int countSetFlags(int p_77907_0_) {
        int i = 0;
        while (p_77907_0_ > 0) {
            p_77907_0_ &= p_77907_0_ - 1;
            ++i;
        }
        return i;
    }

    private static int parsePotionEffects(String p_77912_0_, int p_77912_1_, int p_77912_2_, int p_77912_3_) {
        if (p_77912_1_ < p_77912_0_.length() && p_77912_2_ >= 0 && p_77912_1_ < p_77912_2_) {
            int i = p_77912_0_.indexOf(124, p_77912_1_);
            if (i >= 0 && i < p_77912_2_) {
                int l1 = PotionHelper.parsePotionEffects(p_77912_0_, p_77912_1_, i - 1, p_77912_3_);
                if (l1 > 0) {
                    return l1;
                }
                int i2 = PotionHelper.parsePotionEffects(p_77912_0_, i + 1, p_77912_2_, p_77912_3_);
                return i2 > 0 ? i2 : 0;
            }
            int j = p_77912_0_.indexOf(38, p_77912_1_);
            if (j >= 0 && j < p_77912_2_) {
                int k = PotionHelper.parsePotionEffects(p_77912_0_, p_77912_1_, j - 1, p_77912_3_);
                if (k <= 0) {
                    return 0;
                }
                int j2 = PotionHelper.parsePotionEffects(p_77912_0_, j + 1, p_77912_2_, p_77912_3_);
                return j2 <= 0 ? 0 : (k > j2 ? k : j2);
            }
            boolean flag = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            int b0 = -1;
            int l = 0;
            int i1 = 0;
            int j1 = 0;
            for (int k1 = p_77912_1_; k1 < p_77912_2_; ++k1) {
                char c0 = p_77912_0_.charAt(k1);
                if (c0 >= '0' && c0 <= '9') {
                    if (flag) {
                        i1 = c0 - 48;
                        flag1 = true;
                        continue;
                    }
                    l *= 10;
                    l += c0 - 48;
                    flag2 = true;
                    continue;
                }
                if (c0 == '*') {
                    flag = true;
                    continue;
                }
                if (c0 == '!') {
                    if (flag2) {
                        j1 += PotionHelper.func_77904_a(flag3, flag1, flag4, b0, l, i1, p_77912_3_);
                        flag3 = false;
                        flag4 = false;
                        flag = false;
                        flag1 = false;
                        flag2 = false;
                        i1 = 0;
                        l = 0;
                        b0 = -1;
                    }
                    flag3 = true;
                    continue;
                }
                if (c0 == '-') {
                    if (flag2) {
                        j1 += PotionHelper.func_77904_a(flag3, flag1, flag4, b0, l, i1, p_77912_3_);
                        flag3 = false;
                        flag4 = false;
                        flag = false;
                        flag1 = false;
                        flag2 = false;
                        i1 = 0;
                        l = 0;
                        b0 = -1;
                    }
                    flag4 = true;
                    continue;
                }
                if (c0 != '=' && c0 != '<' && c0 != '>') {
                    if (c0 != '+' || !flag2) continue;
                    j1 += PotionHelper.func_77904_a(flag3, flag1, flag4, b0, l, i1, p_77912_3_);
                    flag3 = false;
                    flag4 = false;
                    flag = false;
                    flag1 = false;
                    flag2 = false;
                    i1 = 0;
                    l = 0;
                    b0 = -1;
                    continue;
                }
                if (flag2) {
                    j1 += PotionHelper.func_77904_a(flag3, flag1, flag4, b0, l, i1, p_77912_3_);
                    flag3 = false;
                    flag4 = false;
                    flag = false;
                    flag1 = false;
                    flag2 = false;
                    i1 = 0;
                    l = 0;
                    b0 = -1;
                }
                if (c0 == '=') {
                    b0 = 0;
                    continue;
                }
                if (c0 == '<') {
                    b0 = 2;
                    continue;
                }
                if (c0 != '>') continue;
                b0 = 1;
            }
            if (flag2) {
                j1 += PotionHelper.func_77904_a(flag3, flag1, flag4, b0, l, i1, p_77912_3_);
            }
            return j1;
        }
        return 0;
    }

    public static List getPotionEffects(int p_77917_0_, boolean p_77917_1_) {
        ArrayList arraylist = null;
        for (Potion potion : Potion.potionTypes) {
            int i;
            String s;
            if (potion == null || potion.isUsable() && !p_77917_1_ || (s = (String)potionRequirements.get(potion.getId())) == null || (i = PotionHelper.parsePotionEffects(s, 0, s.length(), p_77917_0_)) <= 0) continue;
            int j = 0;
            String s1 = (String)potionAmplifiers.get(potion.getId());
            if (s1 != null && (j = PotionHelper.parsePotionEffects(s1, 0, s1.length(), p_77917_0_)) < 0) {
                j = 0;
            }
            if (potion.isInstant()) {
                i = 1;
            } else {
                i = 1200 * (i * 3 + (i - 1) * 2);
                i >>= j;
                i = (int)Math.round((double)i * potion.getEffectiveness());
                if ((p_77917_0_ & 0x4000) != 0) {
                    i = (int)Math.round((double)i * 0.75 + 0.5);
                }
            }
            if (arraylist == null) {
                arraylist = Lists.newArrayList();
            }
            PotionEffect potioneffect = new PotionEffect(potion.getId(), i, j);
            if ((p_77917_0_ & 0x4000) != 0) {
                potioneffect.setSplashPotion(true);
            }
            arraylist.add(potioneffect);
        }
        return arraylist;
    }

    private static int brewBitOperations(int p_77906_0_, int p_77906_1_, boolean p_77906_2_, boolean p_77906_3_, boolean p_77906_4_) {
        if (p_77906_4_) {
            if (!PotionHelper.checkFlag(p_77906_0_, p_77906_1_)) {
                return 0;
            }
        } else {
            p_77906_0_ = p_77906_2_ ? (p_77906_0_ &= ~(1 << p_77906_1_)) : (p_77906_3_ ? ((p_77906_0_ & 1 << p_77906_1_) == 0 ? (p_77906_0_ |= 1 << p_77906_1_) : (p_77906_0_ &= ~(1 << p_77906_1_))) : (p_77906_0_ |= 1 << p_77906_1_));
        }
        return p_77906_0_;
    }

    public static int applyIngredient(int p_77913_0_, String p_77913_1_) {
        int b0 = 0;
        int i = p_77913_1_.length();
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        int j = 0;
        for (int k = b0; k < i; ++k) {
            char c0 = p_77913_1_.charAt(k);
            if (c0 >= '0' && c0 <= '9') {
                j *= 10;
                j += c0 - 48;
                flag = true;
                continue;
            }
            if (c0 == '!') {
                if (flag) {
                    p_77913_0_ = PotionHelper.brewBitOperations(p_77913_0_, j, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    j = 0;
                }
                flag1 = true;
                continue;
            }
            if (c0 == '-') {
                if (flag) {
                    p_77913_0_ = PotionHelper.brewBitOperations(p_77913_0_, j, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    j = 0;
                }
                flag2 = true;
                continue;
            }
            if (c0 == '+') {
                if (!flag) continue;
                p_77913_0_ = PotionHelper.brewBitOperations(p_77913_0_, j, flag2, flag1, flag3);
                flag3 = false;
                flag1 = false;
                flag2 = false;
                flag = false;
                j = 0;
                continue;
            }
            if (c0 != '&') continue;
            if (flag) {
                p_77913_0_ = PotionHelper.brewBitOperations(p_77913_0_, j, flag2, flag1, flag3);
                flag3 = false;
                flag1 = false;
                flag2 = false;
                flag = false;
                j = 0;
            }
            flag3 = true;
        }
        if (flag) {
            p_77913_0_ = PotionHelper.brewBitOperations(p_77913_0_, j, flag2, flag1, flag3);
        }
        return p_77913_0_ & Short.MAX_VALUE;
    }

    public static int func_77908_a(int p_77908_0_, int p_77908_1_, int p_77908_2_, int p_77908_3_, int p_77908_4_, int p_77908_5_) {
        return (PotionHelper.checkFlag(p_77908_0_, p_77908_1_) ? 16 : 0) | (PotionHelper.checkFlag(p_77908_0_, p_77908_2_) ? 8 : 0) | (PotionHelper.checkFlag(p_77908_0_, p_77908_3_) ? 4 : 0) | (PotionHelper.checkFlag(p_77908_0_, p_77908_4_) ? 2 : 0) | (PotionHelper.checkFlag(p_77908_0_, p_77908_5_) ? 1 : 0);
    }

    public static void clearPotionColorCache() {
        DATAVALUE_COLORS.clear();
    }

    static {
        potionRequirements.put(Potion.regeneration.getId(), "0 & !1 & !2 & !3 & 0+6");
        potionRequirements.put(Potion.moveSpeed.getId(), "!0 & 1 & !2 & !3 & 1+6");
        potionRequirements.put(Potion.fireResistance.getId(), "0 & 1 & !2 & !3 & 0+6");
        potionRequirements.put(Potion.heal.getId(), "0 & !1 & 2 & !3");
        potionRequirements.put(Potion.poison.getId(), "!0 & !1 & 2 & !3 & 2+6");
        potionRequirements.put(Potion.weakness.getId(), "!0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.harm.getId(), "!0 & !1 & 2 & 3");
        potionRequirements.put(Potion.moveSlowdown.getId(), "!0 & 1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.damageBoost.getId(), "0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Potion.nightVision.getId(), "!0 & 1 & 2 & !3 & 2+6");
        potionRequirements.put(Potion.invisibility.getId(), "!0 & 1 & 2 & 3 & 2+6");
        potionRequirements.put(Potion.waterBreathing.getId(), "0 & !1 & 2 & 3 & 2+6");
        potionRequirements.put(Potion.jump.getId(), "0 & 1 & !2 & 3 & 3+6");
        potionAmplifiers.put(Potion.moveSpeed.getId(), "5");
        potionAmplifiers.put(Potion.digSpeed.getId(), "5");
        potionAmplifiers.put(Potion.damageBoost.getId(), "5");
        potionAmplifiers.put(Potion.regeneration.getId(), "5");
        potionAmplifiers.put(Potion.harm.getId(), "5");
        potionAmplifiers.put(Potion.heal.getId(), "5");
        potionAmplifiers.put(Potion.resistance.getId(), "5");
        potionAmplifiers.put(Potion.poison.getId(), "5");
        potionAmplifiers.put(Potion.jump.getId(), "5");
    }
}

