package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
    /**
     * The id number for the next tick entry
     */
    private static long nextTickEntryID;
    private final Block block;
    public final BlockPos position;

    /**
     * Time this tick is scheduled to occur at
     */
    public long scheduledTime;
    public int priority;

    /**
     * The id of the tick entry
     */
    private final long tickEntryID;

    public NextTickListEntry(final BlockPos p_i45745_1_, final Block p_i45745_2_) {
        this.tickEntryID = nextTickEntryID++;
        this.position = p_i45745_1_;
        this.block = p_i45745_2_;
    }

    public boolean equals(final Object p_equals_1_) {
        if (!(p_equals_1_ instanceof NextTickListEntry)) {
            return false;
        } else {
            final NextTickListEntry nextticklistentry = (NextTickListEntry) p_equals_1_;
            return this.position.equals(nextticklistentry.position) && Block.isEqualTo(this.block, nextticklistentry.block);
        }
    }

    public int hashCode() {
        return this.position.hashCode();
    }

    /**
     * Sets the scheduled time for this tick entry
     */
    public NextTickListEntry setScheduledTime(final long p_77176_1_) {
        this.scheduledTime = p_77176_1_;
        return this;
    }

    public void setPriority(final int p_82753_1_) {
        this.priority = p_82753_1_;
    }

    public int compareTo(final NextTickListEntry p_compareTo_1_) {
        return this.scheduledTime < p_compareTo_1_.scheduledTime ? -1 : (this.scheduledTime > p_compareTo_1_.scheduledTime ? 1 : (this.priority != p_compareTo_1_.priority ? this.priority - p_compareTo_1_.priority : (this.tickEntryID < p_compareTo_1_.tickEntryID ? -1 : (this.tickEntryID > p_compareTo_1_.tickEntryID ? 1 : 0))));
    }

    public String toString() {
        return Block.getIdFromBlock(this.block) + ": " + this.position + ", " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
    }

    public Block getBlock() {
        return this.block;
    }
}
