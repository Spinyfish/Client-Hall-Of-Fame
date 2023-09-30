/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 */
package net.optifine;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet
extends TreeSet {
    private LongHashMap longHashMap = new LongHashMap();
    private int minX = Integer.MIN_VALUE;
    private int minZ = Integer.MIN_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxZ = Integer.MIN_VALUE;
    private static final int UNDEFINED = Integer.MIN_VALUE;

    public NextTickHashSet(Set oldSet) {
        for (Object object : oldSet) {
            this.add(object);
        }
    }

    @Override
    public boolean contains(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }
        NextTickListEntry nextticklistentry = (NextTickListEntry)obj;
        Set set = this.getSubSet(nextticklistentry, false);
        return set == null ? false : set.contains(nextticklistentry);
    }

    @Override
    public boolean add(Object obj) {
        boolean flag1;
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }
        NextTickListEntry nextticklistentry = (NextTickListEntry)obj;
        if (nextticklistentry == null) {
            return false;
        }
        Set set = this.getSubSet(nextticklistentry, true);
        boolean flag = set.add(nextticklistentry);
        if (flag != (flag1 = super.add(obj))) {
            throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
        }
        return flag1;
    }

    @Override
    public boolean remove(Object obj) {
        boolean flag1;
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }
        NextTickListEntry nextticklistentry = (NextTickListEntry)obj;
        Set set = this.getSubSet(nextticklistentry, false);
        if (set == null) {
            return false;
        }
        boolean flag = set.remove(nextticklistentry);
        if (flag != (flag1 = super.remove(nextticklistentry))) {
            throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
        }
        return flag1;
    }

    private Set getSubSet(NextTickListEntry entry, boolean autoCreate) {
        if (entry == null) {
            return null;
        }
        BlockPos blockpos = entry.position;
        int i = blockpos.getX() >> 4;
        int j = blockpos.getZ() >> 4;
        return this.getSubSet(i, j, autoCreate);
    }

    private Set getSubSet(int cx, int cz, boolean autoCreate) {
        long i = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
        HashSet hashset = (HashSet)this.longHashMap.getValueByKey(i);
        if (hashset == null && autoCreate) {
            hashset = new HashSet();
            this.longHashMap.add(i, hashset);
        }
        return hashset;
    }

    @Override
    public Iterator iterator() {
        if (this.minX == Integer.MIN_VALUE) {
            return super.iterator();
        }
        if (this.size() <= 0) {
            return Iterators.emptyIterator();
        }
        int i = this.minX >> 4;
        int j = this.minZ >> 4;
        int k = this.maxX >> 4;
        int l = this.maxZ >> 4;
        ArrayList list = new ArrayList();
        for (int i1 = i; i1 <= k; ++i1) {
            for (int j1 = j; j1 <= l; ++j1) {
                Set set = this.getSubSet(i1, j1, false);
                if (set == null) continue;
                list.add(set.iterator());
            }
        }
        if (list.size() <= 0) {
            return Iterators.emptyIterator();
        }
        if (list.size() == 1) {
            return (Iterator)list.get(0);
        }
        return Iterators.concat(list.iterator());
    }

    public void setIteratorLimits(int minX, int minZ, int maxX, int maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public void clearIteratorLimits() {
        this.minX = Integer.MIN_VALUE;
        this.minZ = Integer.MIN_VALUE;
        this.maxX = Integer.MIN_VALUE;
        this.maxZ = Integer.MIN_VALUE;
    }
}

