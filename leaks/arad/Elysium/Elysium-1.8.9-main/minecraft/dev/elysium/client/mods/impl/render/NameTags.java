package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.events.EventRenderNameTag;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.Arrays;
import java.util.List;

public class NameTags extends Mod {
    public NumberSetting tagscale = new NumberSetting("Scale",1,3,1,0.05,this);
    public ModeSetting mode = new ModeSetting("Mode",this,"Vape","Elysium","Name","Astolfo","Exhibition");
    public NameTags() {
        super("NameTags","Creates tags with information about targets", Category.RENDER);
    }

    @EventTarget
    public void onEventRenderNameTag(EventRenderNameTag e) {
        if(Elysium.getInstance().getTargets().contains(e.getEntity()))
            e.setCancelled(true);
    }

    @EventTarget
    public void onEventRenderHUD(EventRenderHUD e) {
        ScaledResolution sr = new ScaledResolution(mc);
        ESP esp = (ESP) Elysium.getInstance().getModManager().getModByName("ESP");
        boolean nc = esp.toggled && esp.mode2d.isVisible();
        double scale = 3-tagscale.getValue();
        FontRenderer fr = mc.fontRendererObj;
        List<EntityLivingBase> targets = Elysium.getInstance().getTargets();
        if(mc.gameSettings.thirdPersonView != 0)
            targets.add(mc.thePlayer);
        for(EntityLivingBase en : targets) {
            double posX = RenderUtils.interpolate(en.posX, en.lastTickPosX, e.getPartialTicks());
            double posY = RenderUtils.interpolate(en.posY, en.lastTickPosY, e.getPartialTicks()) - scale/30 + en.getDistance(mc.getRenderManager().viewerPosX,mc.getRenderManager().viewerPosY,mc.getRenderManager().viewerPosZ)/(40*scale);
            double posZ = RenderUtils.interpolate(en.posZ, en.lastTickPosZ, e.getPartialTicks());

            double width = en.width / 1.5;
            double height = en.getEyeHeight() * 1.3185;

            Vector4d pos = null;

            AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
            List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

            mc.entityRenderer.setupCameraTransform((float) e.getPartialTicks(), 0);
            
            for (Vector3d vector : vectors) {
                if(nc)
                    vector = RenderUtils.project(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                else
                    vector = RenderUtils.project(en.posX - mc.getRenderManager().viewerPosX, posY + height - mc.getRenderManager().viewerPosY, en.posZ - mc.getRenderManager().viewerPosZ);
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

            mc.entityRenderer.setupOverlayRendering();;

            if (pos != null) {
                final float x = (float) pos.x;
                final float x2 = (float) pos.z;
                final float y = (float) pos.y - 1;
                GlStateManager.translate((x + ((x2 - x) / 2)), y,1);
                GlStateManager.scale(1/scale, 1/scale, 1);

                switch(mode.getMode()) {
                    case "Vape":
                        drawVape(en,fr,height);
                        break;
                    case "Name":
                        fr.drawStringWithShadow(en.getName(),-fr.getStringWidth(en.getName())/2,fr.FONT_HEIGHT,-1);
                        break;
                    case "Astolfo":
                        drawAstolfo(en,fr,height);
                        break;
                    case "Exhibition":
                        drawExhi(en,fr,height);
                        break;
                    case "Elysium":
                        drawEly(en,fr,height);
                        break;
                }

                GlStateManager.scale(1*scale, 1*scale, 1);
                GlStateManager.translate(-(x + ((x2 - x) / 2)), -y,1);
            }
        }
    }

    private void drawEly(EntityLivingBase en, FontRenderer fr, double height) {
        fr.drawStringWithShadow("Under construction",-fr.getStringWidth("Under construction")/2,fr.FONT_HEIGHT,-1);
    }

    private void drawExhi(EntityLivingBase en, FontRenderer fr, double height) {
        fr.drawStringWithShadow("Under construction",-fr.getStringWidth("Under construction")/2,fr.FONT_HEIGHT,-1);
    }

    public void drawAstolfo(EntityLivingBase en, FontRenderer fr, double height) {
        String healthtxt = (Math.round(en.getHealth()*10.0f)/10.0f)+"";
        String disttxt = (Math.round(mc.thePlayer.getDistanceToEntity(en)*10.0f)/10.0f)+"";
        String text = en.getName()+" \u00A77[\u00A7f"+healthtxt+"\u00A7c\u2764\u00A77] \u00A7 - \u00A77"+disttxt+"m";

        Gui.drawRect(-fr.getStringWidth(text)/2-3, height-2, fr.getStringWidth(text)/2+3, fr.FONT_HEIGHT+2+height, 0x99000000);

        fr.drawCenteredString(text, 0, (float) (height+0.5), -1);
    }

    public void drawVape(EntityLivingBase en, FontRenderer fr, double height) {
        EnumChatFormatting g = EnumChatFormatting.GREEN;
        EnumChatFormatting r = EnumChatFormatting.RED;
        EnumChatFormatting b = EnumChatFormatting.BLUE;
        EnumChatFormatting re = EnumChatFormatting.RESET;

        int dist = Math.round(mc.thePlayer.getDistanceToEntity(en));

        String name = en.getDisplayName().getFormattedText();
        float health = Math.round(en.getHealth()*5)/10F;

        String hsymbol = en.getHealth() == mc.thePlayer.getTotalArmorValue() ? EnumChatFormatting.YELLOW + "=" : en.getTotalArmorValue() > mc.thePlayer.getTotalArmorValue() ? EnumChatFormatting.RED + "-" : EnumChatFormatting.GREEN + "+";

        float dmgper = en.getHealth()/en.getMaxHealth()*100;

        int colour = 0xFF05a102;

        String healthlength = "";

        for(int x1 = 0; x1 <= Float.toString(health).length()+1; x1++) healthlength += " ";


        if(dmgper < 70) colour = 0xFFf3ff68;
        if(dmgper <= 50) colour = 0xFFe1b121;
        if(dmgper <= 20) colour = 0xFF980813;
        String contentsnh = g + "[" + re + dist + g + "] " + name + re + " ";
        String contents = g + "[" + re + dist + g + "] " + name + re + " " + healthlength + " " + hsymbol;
        double twidth = fr.getStringWidth(contents);
        Gui.drawRect(-twidth/2 - 2, -2 + height-2, twidth/2 + 2, fr.FONT_HEIGHT + .5 + height-2, 0x88000000);

        fr.drawCenteredString(contents, 0, (float) (height-2), -1);
        fr.drawString(Float.toString(health), (int) (-twidth / 2 + fr.getStringWidth(contentsnh) + 0.5), (int) (height-2), colour);

    }
}
