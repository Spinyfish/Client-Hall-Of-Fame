package de.verschwiegener.atero.module.modules.render;


import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPostRender;
import com.darkmagician6.eventapi.events.callables.EventRender2D;

import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class ChestESP extends Module {
    private float oldBrightness;


    public ChestESP() {
        super("ChestESP", "ChestESP", Keyboard.KEY_NONE, Category.Render);
    }

    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            mc.gameSettings.gammaSetting = oldBrightness;

            mc.gameSettings.gammaSetting = 10F;
        }
    }

    @EventTarget
    public void onPostRender(EventPostRender render) {

    }

    public static void drawChestESP() {
            List<TileEntity> loadedTileEntityList = Minecraft.getMinecraft().theWorld.loadedTileEntityList;
            for (int i = 0, loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; i++) {
                TileEntity tileEntity = loadedTileEntityList.get(i);
                if (tileEntity instanceof TileEntityChest) {
                    GlStateManager.disableTexture2D();
                    GL11.glColor4f(0, 255, 255, 255);
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity,
                            Minecraft.getMinecraft().timer.renderPartialTicks, 1);
                    GlStateManager.enableTexture2D();

            }

        }
    }
}
