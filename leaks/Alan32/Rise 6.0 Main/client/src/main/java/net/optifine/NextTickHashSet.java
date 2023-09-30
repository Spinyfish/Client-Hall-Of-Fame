package net.optifine;

import com.google.common.collect.Iterators;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

import java.util.*;

public class NextTickHashSet extends TreeSet {
    private final LongHashMap longHashMap = new LongHashMap();
    private int minX = Integer.MIN_VALUE;
    private int minZ = Integer.MIN_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxZ = Integer.MIN_VALUE;
    private static final int UNDEFINED = Integer.MIN_VALUE;

    public NextTickHashSet(final Set oldSet) {
        for (final Object object : oldSet) {
            this.add(object);
        }
    }

    public boolean contains(final Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        } else {
            final NextTickListEntry nextticklistentry = (NextTickListEntry) obj;
            final Set set = this.getSubSet(nextticklistentry, false);
            return set != null && set.contains(nextticklistentry);
        }
    }

    public boolean add(final Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        } else {
            final NextTickListEntry nextticklistentry = (NextTickListEntry) obj;

            if (nextticklistentry == null) {
                return false;
            } else {
                final Set set = this.getSubSet(nextticklistentry, true);
                final boolean flag = set.add(nextticklistentry);
                final boolean flag1 = super.add(obj);

                if (flag != flag1) {
                    throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
                } else {
                    return flag1;
                }
            }
        }
    }

    public boolean remove(final Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        } else {
            final NextTickListEntry nextticklistentry = (NextTickListEntry) obj;
            final Set set = this.getSubSet(nextticklistentry, false);

            if (set == null) {
                return false;
            } else {
                final boolean flag = set.remove(nextticklistentry);
                final boolean flag1 = super.remove(nextticklistentry);

                if (flag != flag1) {
                    throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
                } else {
                    return flag1;
                }
            }
        }
    }

    private Set getSubSet(final NextTickListEntry entry, final boolean autoCreate) {
        if (entry == null) {
            return null;
        } else {
            final BlockPos blockpos = entry.position;
            final int i = blockpos.getX() >> 4;
            final int j = blockpos.getZ() >> 4;
            return this.getSubSet(i, j, autoCreate);
        }
    }

    private Set getSubSet(final int cx, final int cz, final boolean autoCreate) {
        final long i = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
        HashSet hashset = (HashSet) this.longHashMap.getValueByKey(i);

        if (hashset == null && autoCreate) {
            hashset = new HashSet();
            this.longHashMap.add(i, hashset);
        }

        return hashset;
    }

    public Iterator iterator() {
        if (this.minX == Integer.MIN_VALUE) {
            return super.iterator();
        } else if (this.size() <= 0) {
            return Iterators.emptyIterator();
        } else {
            final int i = this.minX >> 4;
            final int j = this.minZ >> 4;
            final int k = this.maxX >> 4;
            final int l = this.maxZ >> 4;
            final List list = new ArrayList();

            for (int i1 = i; i1 <= k; ++i1) {
                for (int j1 = j; j1 <= l; ++j1) {
                    final Set set = this.getSubSet(i1, j1, false);

                    if (set != null) {
                        list.add(set.iterator());
                    }
                }
            }

            if (list.size() <= 0) {
                return Iterators.emptyIterator();
            } else if (list.size() == 1) {
                return (Iterator) list.get(0);
            } else {
                return Iterators.concat(list.iterator());
            }
        }
    }

    public void setIteratorLimits(final int minX, final int minZ, final int maxX, final int maxZ) {
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
