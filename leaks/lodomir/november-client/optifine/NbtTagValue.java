/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package optifine;

import java.util.Arrays;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import optifine.Config;
import optifine.StrUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue {
    private String[] parents = null;
    private String name = null;
    private int type = 0;
    private String value = null;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PATTERN = 1;
    private static final int TYPE_IPATTERN = 2;
    private static final int TYPE_REGEX = 3;
    private static final int TYPE_IREGEX = 4;
    private static final String PREFIX_PATTERN = "pattern:";
    private static final String PREFIX_IPATTERN = "ipattern:";
    private static final String PREFIX_REGEX = "regex:";
    private static final String PREFIX_IREGEX = "iregex:";

    public NbtTagValue(String p_i69_1_, String p_i69_2_) {
        String[] astring = Config.tokenize(p_i69_1_, ".");
        this.parents = Arrays.copyOfRange(astring, 0, astring.length - 1);
        this.name = astring[astring.length - 1];
        if (p_i69_2_.startsWith(PREFIX_PATTERN)) {
            this.type = 1;
            p_i69_2_ = p_i69_2_.substring(PREFIX_PATTERN.length());
        } else if (p_i69_2_.startsWith(PREFIX_IPATTERN)) {
            this.type = 2;
            p_i69_2_ = p_i69_2_.substring(PREFIX_IPATTERN.length()).toLowerCase();
        } else if (p_i69_2_.startsWith(PREFIX_REGEX)) {
            this.type = 3;
            p_i69_2_ = p_i69_2_.substring(PREFIX_REGEX.length());
        } else if (p_i69_2_.startsWith(PREFIX_IREGEX)) {
            this.type = 4;
            p_i69_2_ = p_i69_2_.substring(PREFIX_IREGEX.length()).toLowerCase();
        } else {
            this.type = 0;
        }
        this.value = p_i69_2_ = StringEscapeUtils.unescapeJava((String)p_i69_2_);
    }

    public boolean matches(NBTTagCompound p_matches_1_) {
        if (p_matches_1_ == null) {
            return false;
        }
        NBTBase nbtbase = p_matches_1_;
        for (int i = 0; i < this.parents.length; ++i) {
            String s = this.parents[i];
            if ((nbtbase = NbtTagValue.getChildTag(nbtbase, s)) != null) continue;
            return false;
        }
        if (this.name.equals("*")) {
            return this.matchesAnyChild(nbtbase);
        }
        if ((nbtbase = NbtTagValue.getChildTag(nbtbase, this.name)) == null) {
            return false;
        }
        return this.matches(nbtbase);
    }

    private boolean matchesAnyChild(NBTBase p_matchesAnyChild_1_) {
        if (p_matchesAnyChild_1_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_matchesAnyChild_1_;
            for (String s : nbttagcompound.getKeySet()) {
                NBTBase nbtbase = nbttagcompound.getTag(s);
                if (!this.matches(nbtbase)) continue;
                return true;
            }
        }
        if (p_matchesAnyChild_1_ instanceof NBTTagList) {
            NBTTagList nbttaglist = (NBTTagList)p_matchesAnyChild_1_;
            int i = nbttaglist.tagCount();
            for (int j = 0; j < i; ++j) {
                NBTBase nbtbase1 = nbttaglist.get(j);
                if (!this.matches(nbtbase1)) continue;
                return true;
            }
        }
        return false;
    }

    private static NBTBase getChildTag(NBTBase p_getChildTag_0_, String p_getChildTag_1_) {
        if (p_getChildTag_0_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_getChildTag_0_;
            return nbttagcompound.getTag(p_getChildTag_1_);
        }
        if (p_getChildTag_0_ instanceof NBTTagList) {
            NBTTagList nbttaglist = (NBTTagList)p_getChildTag_0_;
            int i = Config.parseInt(p_getChildTag_1_, -1);
            return i < 0 ? null : nbttaglist.get(i);
        }
        return null;
    }

    private boolean matches(NBTBase p_matches_1_) {
        if (p_matches_1_ == null) {
            return false;
        }
        String s = NbtTagValue.getValue(p_matches_1_);
        if (s == null) {
            return false;
        }
        switch (this.type) {
            case 0: {
                return s.equals(this.value);
            }
            case 1: {
                return this.matchesPattern(s, this.value);
            }
            case 2: {
                return this.matchesPattern(s.toLowerCase(), this.value);
            }
            case 3: {
                return this.matchesRegex(s, this.value);
            }
            case 4: {
                return this.matchesRegex(s.toLowerCase(), this.value);
            }
        }
        throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
    }

    private boolean matchesPattern(String p_matchesPattern_1_, String p_matchesPattern_2_) {
        return StrUtils.equalsMask(p_matchesPattern_1_, p_matchesPattern_2_, '*', '?');
    }

    private boolean matchesRegex(String p_matchesRegex_1_, String p_matchesRegex_2_) {
        return p_matchesRegex_1_.matches(p_matchesRegex_2_);
    }

    private static String getValue(NBTBase p_getValue_0_) {
        if (p_getValue_0_ == null) {
            return null;
        }
        if (p_getValue_0_ instanceof NBTTagString) {
            NBTTagString nbttagstring = (NBTTagString)p_getValue_0_;
            return nbttagstring.getString();
        }
        if (p_getValue_0_ instanceof NBTTagInt) {
            NBTTagInt nbttagint = (NBTTagInt)p_getValue_0_;
            return Integer.toString(nbttagint.getInt());
        }
        if (p_getValue_0_ instanceof NBTTagByte) {
            NBTTagByte nbttagbyte = (NBTTagByte)p_getValue_0_;
            return Byte.toString(nbttagbyte.getByte());
        }
        if (p_getValue_0_ instanceof NBTTagShort) {
            NBTTagShort nbttagshort = (NBTTagShort)p_getValue_0_;
            return Short.toString(nbttagshort.getShort());
        }
        if (p_getValue_0_ instanceof NBTTagLong) {
            NBTTagLong nbttaglong = (NBTTagLong)p_getValue_0_;
            return Long.toString(nbttaglong.getLong());
        }
        if (p_getValue_0_ instanceof NBTTagFloat) {
            NBTTagFloat nbttagfloat = (NBTTagFloat)p_getValue_0_;
            return Float.toString(nbttagfloat.getFloat());
        }
        if (p_getValue_0_ instanceof NBTTagDouble) {
            NBTTagDouble nbttagdouble = (NBTTagDouble)p_getValue_0_;
            return Double.toString(nbttagdouble.getDouble());
        }
        return p_getValue_0_.toString();
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < this.parents.length; ++i) {
            String s = this.parents[i];
            if (i > 0) {
                stringbuffer.append(".");
            }
            stringbuffer.append(s);
        }
        if (stringbuffer.length() > 0) {
            stringbuffer.append(".");
        }
        stringbuffer.append(this.name);
        stringbuffer.append(" = ");
        stringbuffer.append(this.value);
        return stringbuffer.toString();
    }
}

