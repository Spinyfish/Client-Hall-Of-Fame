package net.minecraft.src;

import net.minecraft.block.state.BlockStateBase;

public class MatchBlock
{
    private int blockId = -1;
    private int[] metadatas = null;

    public MatchBlock(int blockId)
    {
        this.blockId = blockId;
    }

    public MatchBlock(int blockId, int metadata)
    {
        this.blockId = blockId;

        if (metadata >= 0 && metadata <= 15)
        {
            this.metadatas = new int[] {metadata};
        }
    }

    public MatchBlock(int blockId, int[] metadatas)
    {
        this.blockId = blockId;
        this.metadatas = metadatas;
    }

    public int getBlockId()
    {
        return this.blockId;
    }

    public int[] getMetadatas()
    {
        return this.metadatas;
    }

    public boolean matches(BlockStateBase blockState)
    {
        if (blockState.getBlockId() != this.blockId)
        {
            return false;
        }
        else
        {
            if (this.metadatas != null)
            {
                boolean matchMetadata = false;
                int metadata = blockState.getMetadata();

                for (int i = 0; i < this.metadatas.length; ++i)
                {
                    int md = this.metadatas[i];

                    if (md == metadata)
                    {
                        matchMetadata = true;
                        break;
                    }
                }

                if (!matchMetadata)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public void addMetadata(int metadata)
    {
        if (this.metadatas != null)
        {
            if (metadata >= 0 && metadata <= 15)
            {
                for (int i = 0; i < this.metadatas.length; ++i)
                {
                    if (this.metadatas[i] == metadata)
                    {
                        return;
                    }
                }

                this.metadatas = Config.addIntToArray(this.metadatas, metadata);
            }
        }
    }

    public String toString()
    {
        return "" + this.blockId + ":" + Config.arrayToString(this.metadatas);
    }
}
