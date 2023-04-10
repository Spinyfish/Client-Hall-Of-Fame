package crispy.features.hacks.impl.render;

import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

@HackInfo(name = "ChestESP", category = Category.RENDER)
public class ChestESP extends Hack {
    @Override
    public void onEvent(Event e) {
        if(e instanceof Event3D) {
            for(Object o : Minecraft.theWorld.loadedTileEntityList) {
                if(!(o instanceof TileEntity))
                    return;
                BlockPos pos = new BlockPos(((TileEntity)o).getPos());
                double x = pos.getX() - RenderManager.renderPosX;
                double y = pos.getY() - RenderManager.renderPosY;
                double z = pos.getZ() - RenderManager.renderPosZ;
                if (o instanceof TileEntityChest) {
                    RenderUtils.drawOutlinedBlockESP(x, y, z, 1f, 1f, 1f, 1f, 1f);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                if (o instanceof TileEntityEnderChest) {
                    RenderUtils.drawOutlinedBlockESP(x, y, z, 1f, 0.3f, 1f, 1f, 1f);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);


                }
            }
        }
    }
}
