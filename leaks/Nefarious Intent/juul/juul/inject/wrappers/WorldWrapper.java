package juul.inject.wrappers;

import java.awt.image.BufferedImage;
import java.util.List;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldWrapper extends Wrapper {
	
	public static Object instance;
	private static String path = "net.minecraft.world.World",
			szChunkPath = "net.minecraft.world.chunk.Chunk";
	
	public WorldWrapper(Object instance) {
		super( "net.minecraft.world.World", instance );
	}
	
	public List<?> playerEntities() { return (List<?>) getField("playerEntities"); }

	//Should be in another class
	public IBlockState getBlockState(final BlockPos pos) {

		return (IBlockState) Juul.INSTANCE.getReflectionHelper().getWithClass(getBlockPosName(), getChunkClassName(), null, pos);
	}


	public static String getBlockPosName() { return Juul.INSTANCE.getLookupTable().getMethodName(szChunkPath, "getBlockState"); }

	public static String getClassName() {
		return Juul.INSTANCE.getLookupTable().getClassName(path);
	}
	public static String getChunkClassName() { return Juul.INSTANCE.getLookupTable().getClassName(szChunkPath); }
	
}
