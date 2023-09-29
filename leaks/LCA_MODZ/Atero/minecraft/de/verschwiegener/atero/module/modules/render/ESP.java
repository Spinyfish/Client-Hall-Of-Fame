package de.verschwiegener.atero.module.modules.render;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
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

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ESP extends Module {
    private float oldBrightness;
    public static ESP instance;
    public static Setting setting;
    public ESP() {
	super("ESP", "ESP", Keyboard.KEY_NONE, Category.Render);
	instance = this;
    }

    public void onUpdate() {
	if (this.isEnabled()) {
	    super.onUpdate();
	    setExtraTag("GlowShader");
	    mc.gameSettings.gammaSetting = oldBrightness;

	    mc.gameSettings.gammaSetting = 10F;
	}
    }

    @EventTarget
    public void onRender() {
            ScaledResolution scaledResolution = new ScaledResolution(mc);

                        // HEALTH BAR
                        float health = mc.thePlayer.getHealth();
                        float maxHealth = mc.thePlayer.getMaxHealth();
                        //new Color(0, 255, 0, 255).getRGB()
                     //   RenderUtil.drawRect(posX - 2, endPosY - (health / maxHealth) * (endPosY - posY), posX - 0.5, endPosY, Management.instance.colorBlue.getRGB());
                    }
    @Override
    public void setup() {

        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        modes.add("AAC");
        items.add(new SettingsItem("TEST222", Color.RED, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }
                }


        // }






