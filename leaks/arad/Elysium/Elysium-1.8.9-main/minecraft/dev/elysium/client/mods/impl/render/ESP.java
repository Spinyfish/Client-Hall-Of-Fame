package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.utils.render.ColorUtil;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ESP extends Mod {
    public ModeSetting mode = new ModeSetting("Mode",this,"2D","3D Box");
    public ModeSetting mode2d = new ModeSetting("2D Mode",this,"Fill","Box","Corners 1","Corners 2","Split","Arad");
    public NumberSetting linewidth = new NumberSetting("Line Width",0.5,10,1,0.5,this);
    public BooleanSetting border = new BooleanSetting("Border",true, this);
    public ESP() {
        super("ESP","Extrasensory Perception", Category.RENDER);
        mode2d.setPredicate(mod -> mode.is("2D"));
        linewidth.setPredicate(mod -> mode2d.isVisible() && !mode2d.is("Fill"));
        border.setPredicate(mod -> mode2d.isVisible());
    }

    @EventTarget
    public void onEventRenderHUD(EventRenderHUD e) {
        FontRenderer fr = mc.fontRendererObj;
        double lw = linewidth.getValue();
        boolean sb = border.isEnabled();
        List<EntityLivingBase> targets = Elysium.getInstance().getTargets();
        for(EntityLivingBase en : targets) {
            double posX = RenderUtils.interpolate(en.posX, en.lastTickPosX, e.getPartialTicks());
            double posY = RenderUtils.interpolate(en.posY, en.lastTickPosY, e.getPartialTicks()) - 0.1;
            double posZ = RenderUtils.interpolate(en.posZ, en.lastTickPosZ, e.getPartialTicks());

            double width = en.width / 1.5;
            double height = en.getEyeHeight() * 1.3185;

            Vector4d pos = null;

            AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
            List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

            mc.entityRenderer.setupCameraTransform((float) e.getPartialTicks(), 0);

            for (Vector3d vector : vectors) {
                vector = RenderUtils.project(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                    if (pos == null) {
                        pos = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                    }
                    pos.x = Math.min(vector.x, pos.x);
                    pos.y = Math.min(vector.y, pos.y);
                    pos.z = Math.max(vector.x, pos.z);
                    pos.w = Math.max(vector.y, pos.w);
                }
            }

            mc.entityRenderer.setupOverlayRendering();
            if (pos != null && pos.z >= 1) {
                int colour = ColorUtil.getRainbow(12,0.7F,1F,0);
                pos.y -= mc.thePlayer.getDistanceToEntity(en)/50;
                switch(mode2d.getMode()) {
                    case "Fill":
                        if(sb) {
                            Gui.drawRect(pos.x-0.5, pos.y - 0.5 + height,pos.z + 0.5,pos.w + 0.5, 0xFF000000);
                        }
                        Gui.drawRect(pos.x, pos.y + height,pos.z,pos.w, colour);
                        break;
                    case "Arad":
                        mc.getTextureManager().bindTexture(new ResourceLocation("Elysium/arad.png"));
                        Gui.drawModalRectWithCustomSizedTexture((int) Math.round(pos.x),  (int) Math.round(pos.y), 0, 0, (int) Math.round(pos.z - pos.x), (int) Math.round(pos.w - pos.y), (int) Math.round(pos.x - pos.z), (int) Math.round(pos.w - pos.y));
                        break;
                    case "Box":
                        if(sb) {
                            Gui.drawRect(pos.x - lw - 0.5, pos.w - lw - 0.5,pos.z + lw + 0.5,pos.w + 0.5, 0xFF000000);
                            Gui.drawRect(pos.x - lw - 0.5, pos.y + height - 0.5,pos.z + lw + 0.5,pos.y + height + lw + 0.5, 0xFF000000);

                            Gui.drawRect(pos.x - lw - 0.5, pos.y + height,pos.x + 0.5,pos.w, 0xFF000000);
                            Gui.drawRect(pos.z - 0.5, pos.y + height,pos.z + lw + 0.5,pos.w, 0xFF000000);
                        }

                        Gui.drawRect(pos.x, pos.y + height,pos.z,pos.y + height + lw, colour);
                        Gui.drawRect(pos.x, pos.w - lw,pos.z,pos.w, colour);

                        Gui.drawRect(pos.z, pos.y + height,pos.z + lw,pos.w, colour);
                        Gui.drawRect(pos.x - lw, pos.y + height,pos.x,pos.w, colour);

                        break;
                    case "Split":
                        double widthQ = (pos.z - pos.x)/4;
                        if(sb) {
                            Gui.drawRect(pos.x - lw - 0.5, pos.w - lw - 0.5,pos.x + widthQ + 0.5,pos.w + 0.5, 0xFF000000);
                            Gui.drawRect(pos.x - lw - 0.5, pos.y + height - 0.5,pos.x + widthQ + 0.5,pos.y + height + lw + 0.5, 0xFF000000);
                            Gui.drawRect(pos.z + lw + 0.5, pos.w - lw - 0.5,pos.z - widthQ - 0.5,pos.w + 0.5, 0xFF000000);
                            Gui.drawRect(pos.z + lw + 0.5, pos.y + height - 0.5,pos.z - widthQ - 0.5,pos.y + height + lw + 0.5, 0xFF000000);

                            Gui.drawRect(pos.x - lw - 0.5, pos.y + height,pos.x + 0.5,pos.w, 0xFF000000);
                            Gui.drawRect(pos.z - 0.5, pos.y + height,pos.z + lw + 0.5,pos.w, 0xFF000000);
                        }

                        Gui.drawRect(pos.x, pos.y + height,pos.x + widthQ,pos.y + height + lw, colour);
                        Gui.drawRect(pos.x, pos.w - lw,pos.x + widthQ,pos.w, colour);

                        Gui.drawRect(pos.z, pos.y + height,pos.z - widthQ,pos.y + height + lw, colour);
                        Gui.drawRect(pos.z, pos.w - lw,pos.z - widthQ,pos.w, colour);

                        Gui.drawRect(pos.z, pos.y + height,pos.z + lw,pos.w, colour);
                        Gui.drawRect(pos.x - lw, pos.y + height,pos.x,pos.w, colour);
                        break;
                }

                float hue = 1000 / 360;
                hue *= en.getHealth() * 8;
                hue *= 0.001f;
                int health = Color.HSBtoRGB(hue,1, 1);

                float healtheight = en.getHealth()/en.getMaxHealth();

                if(sb)
                    Gui.drawRect(pos.x - lw - 3, pos.y + (pos.y - pos.w)*healtheight - (pos.y - pos.w) + height - 0.5,pos.x - 2 + 0.5,pos.w + 0.5, 0xFF000000);
                Gui.drawRect(pos.x - lw - 2.5, pos.y + (pos.y - pos.w)*healtheight - (pos.y - pos.w) + height,pos.x - 2,pos.w, health);
            }
        }
    }
}
