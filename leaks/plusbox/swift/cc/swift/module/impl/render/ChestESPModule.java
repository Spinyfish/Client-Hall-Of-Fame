package cc.swift.module.impl.render;

import cc.swift.events.PacketEvent;
import cc.swift.events.RenderWorldEvent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.render.RenderUtil;
import cc.swift.value.impl.ModeValue;
import com.sun.istack.internal.NotNull;
import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ChestESPModule extends Module {



    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public ChestESPModule() {
        super("ChestESP", Category.RENDER);
        registerValues(mode);
    }
    @Handler(EventPriority.LOWEST)
    public final Listener<RenderWorldEvent> renderWorldEventListener = event -> {
        renderChests();
    };

    public void renderChests() {
        Minecraft mc = Minecraft.getMinecraft();

        for (TileEntity te : mc.theWorld.loadedTileEntityList) {
            if (te instanceof TileEntityChest) {


                TileEntityChest chest = (TileEntityChest) te;
                double x = chest.getPos().getX() - mc.getRenderManager().viewerPosX;
                double y = chest.getPos().getY() - mc.getRenderManager().viewerPosY;
                double z = chest.getPos().getZ() - mc.getRenderManager().viewerPosZ;

                AxisAlignedBB boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.color(0.0F, 1.0F, 0.0F, 0.3F);
                GlStateManager.disableDepth();
                switch (mode.getValue()){
                    case BOX:
                        RenderHelper.drawCompleteBoxFilled(boundingBox, 1, new Color(0, 255, 0, 50).getRGB());
                        break;
                    case OUTLINE:
                        RenderGlobal.drawOutlinedBoundingBox(boundingBox, 0, 255, 0, 160);
                        break;

                    case BOTH:
                        RenderHelper.drawCompleteBoxFilled(boundingBox, 1, new Color(0, 255, 0, 50).getRGB());
                        RenderGlobal.drawOutlinedBoundingBox(boundingBox, 0, 255, 0, 160);
                        break;
                }
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    public enum Mode {
        BOTH, OUTLINE, BOX
    }
}
