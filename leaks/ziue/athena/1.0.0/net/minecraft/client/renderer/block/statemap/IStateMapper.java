package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.client.resources.model.*;

public interface IStateMapper
{
    Map<IBlockState, ModelResourceLocation> putStateModelLocations(final Block p0);
}
