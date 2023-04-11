package byron.Mono.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import byron.Mono.event.impl.Event3D;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "ChestESP", description = "See chests!", category = Category.Visual)
public class ChestESP extends Module{
	
    @Override
    public void onEnable() {
        super.onEnable();
        mc.gameSettings.gammaSetting = 1000.0F;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.gammaSetting = 1.0F;
    }
    
    @Subscribe
    public void on3D(Event3D event)
    {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushMatrix();

        int amount = 0;
        for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
                render(amount, tileEntity);
                amount++;
            }
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
    
    private void render(final int amount, final TileEntity p) {
        GL11.glPushMatrix();

        final RenderManager renderManager = mc.getRenderManager();

        final double x = (p.getPos().getX() + 0.5) - renderManager.renderPosX;
        final double y = p.getPos().getY() - renderManager.renderPosY;
        final double z = (p.getPos().getZ() + 0.5) - renderManager.renderPosZ;

        GL11.glTranslated(x, y, z);

        GL11.glRotated(-renderManager.playerViewY, 0.0D, 1.0D, 0.0D);
        GL11.glRotated(renderManager.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

        final float scale = 1 / 100f;
        GL11.glScalef(-scale, -scale, scale);

        final Color c = new Color(255,255,255);

        final float offset = renderManager.playerViewX * 0.5f;

        RenderUtil.lineNoGl(-50, offset, 50, offset, c);
        RenderUtil.lineNoGl(-50, -95 + offset, -50, offset, c);
        RenderUtil.lineNoGl(-50, -95 + offset, 50, -95 + offset, c);
        RenderUtil.lineNoGl(50, -95 + offset, 50, offset, c);

        GL11.glPopMatrix();
    }

}
