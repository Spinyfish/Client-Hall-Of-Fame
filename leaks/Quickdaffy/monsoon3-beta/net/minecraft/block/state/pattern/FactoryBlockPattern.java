/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
    private static final Joiner COMMA_JOIN = Joiner.on((String)",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;

    private FactoryBlockPattern() {
        this.symbolMap.put(Character.valueOf(' '), (Predicate<BlockWorldState>)Predicates.alwaysTrue());
    }

    public FactoryBlockPattern aisle(String ... aisle) {
        if (!ArrayUtils.isEmpty((Object[])aisle) && !StringUtils.isEmpty((CharSequence)aisle[0])) {
            if (this.depth.isEmpty()) {
                this.aisleHeight = aisle.length;
                this.rowWidth = aisle[0].length();
            }
            if (aisle.length != this.aisleHeight) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
            }
            for (String s : aisle) {
                if (s.length() != this.rowWidth) {
                    throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s.length() + ")");
                }
                for (char c0 : s.toCharArray()) {
                    if (this.symbolMap.containsKey(Character.valueOf(c0))) continue;
                    this.symbolMap.put(Character.valueOf(c0), (Predicate<BlockWorldState>)((Predicate)null));
                }
            }
            this.depth.add(aisle);
            return this;
        }
        throw new IllegalArgumentException("Empty pattern for aisle");
    }

    public static FactoryBlockPattern start() {
        return new FactoryBlockPattern();
    }

    public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher) {
        this.symbolMap.put(Character.valueOf(symbol), blockMatcher);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.makePredicateArray());
    }

    private Predicate<BlockWorldState>[][][] makePredicateArray() {
        this.checkMissingPredicates();
        Predicate[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);
        for (int i = 0; i < this.depth.size(); ++i) {
            for (int j = 0; j < this.aisleHeight; ++j) {
                for (int k = 0; k < this.rowWidth; ++k) {
                    predicate[i][j][k] = this.symbolMap.get(Character.valueOf(this.depth.get(i)[j].charAt(k)));
                }
            }
        }
        return predicate;
    }

    private void checkMissingPredicates() {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Character, Predicate<BlockWorldState>> entry : this.symbolMap.entrySet()) {
            if (entry.getValue() != null) continue;
            list.add(entry.getKey());
        }
        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join((Iterable)list) + " are missing");
        }
    }
}

