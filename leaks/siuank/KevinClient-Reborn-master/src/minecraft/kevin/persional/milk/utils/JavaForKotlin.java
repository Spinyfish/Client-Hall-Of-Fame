package kevin.persional.milk.utils;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class JavaForKotlin {
    public static BlockPos getGroundPostion(){
        BlockPos bp = new BlockPos(0, -1, 0);
        for(double lol = 0; lol < 10; lol++){
            if(!(Minecraft.getMinecraft().theWorld.getBlockState(bp.add(0, -lol, 0)).getBlock() instanceof
                    BlockAir)){
                bp = bp.add(0, -lol, 0);
            }
        }
        return bp;
    }
}
