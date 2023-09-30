/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.src.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias {
    private int blockAliasId;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int blockAliasId, MatchBlock[] matchBlocks) {
        this.blockAliasId = blockAliasId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockAliasId() {
        return this.blockAliasId;
    }

    public boolean matches(int id, int metadata) {
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            MatchBlock matchblock = this.matchBlocks[i];
            if (!matchblock.matches(id, metadata)) continue;
            return true;
        }
        return false;
    }

    public int[] getMatchBlockIds() {
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            MatchBlock matchblock = this.matchBlocks[i];
            int j = matchblock.getBlockId();
            set.add(j);
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId) {
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            MatchBlock matchblock = this.matchBlocks[i];
            if (matchblock.getBlockId() != matchBlockId) continue;
            list.add(matchblock);
        }
        MatchBlock[] amatchblock = list.toArray(new MatchBlock[list.size()]);
        return amatchblock;
    }

    public String toString() {
        return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
    }
}

