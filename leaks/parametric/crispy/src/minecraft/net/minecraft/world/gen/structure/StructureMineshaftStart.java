package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class StructureMineshaftStart extends StructureStart
{

    public StructureMineshaftStart() {}

    public StructureMineshaftStart(World worldIn, Random rand, int chunkX, int chunkZ)
    {
        super(chunkX, chunkZ);
        StructureMineshaftPieces.Room var5 = new StructureMineshaftPieces.Room(0, rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
        this.components.add(var5);
        var5.buildComponent(var5, this.components, rand);
        this.updateBoundingBox();
        this.markAvailableHeight(worldIn, rand, 10);
    }
}
