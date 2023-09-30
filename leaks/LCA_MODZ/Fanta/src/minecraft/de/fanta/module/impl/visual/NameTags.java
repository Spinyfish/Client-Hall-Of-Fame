package de.fanta.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.module.Module;
import de.fanta.utils.UnicodeFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;


public class NameTags extends Module {
    public NameTags() {
        super("NameTags", 0, Type.Visual, Color.red);
    }

    final Color back = new Color(	28, 25, 24, 255);
    // Color orange = new Color(173, 129, 101, 255);
    final Color orange = new Color( 255,99,0, 255);
    private String name = "";

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRender3D && event.isPre()){
            final UnicodeFontRenderer font = Client.INSTANCE.fontManager.arial;

            if (mc.thePlayer != null && mc.theWorld != null) {
                for (EntityPlayer e : mc.theWorld.playerEntities) {
                    if (mc.thePlayer.getDistanceToEntity(e) < 250) {
                        if (e != mc.thePlayer) {

                            String health = Math.round(e.getHealth()) + "";

                            name = e.getDisplayName().getFormattedText();

                            name = name.replaceAll("§r", "");

                            GlStateManager.pushMatrix();
                            GlStateManager.enableBlend();
                            GlStateManager.disableDepth();
                            GlStateManager.disableTexture2D();

                            final float pT = mc.timer.renderPartialTicks;
                            //Hier
                            final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * pT - RenderManager.renderPosX;
                            final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * pT - RenderManager.renderPosY;
                            final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pT - RenderManager.renderPosZ;

                            float d = mc.thePlayer.getDistanceToEntity(e);
                            final float s = Math.min(Math.max(1.21f * (d * 0.1F), 1.25F), 6F) * 2 / 100;

                            GlStateManager.translate((float) x, (float) y + e.height + 1.8F - (e.height / 2), (float) z);

                            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);

                            GlStateManager.scale(-s, -s, s);

                            float string_width =  Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(name) / 2 + 2;

                            GlStateManager.enableTexture2D();
                            
                            Gui.drawRect(- Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(name) / 2 - 3, -2, (int) string_width + 8, 13, new Color(30,30,30,140).getRGB());
                            
                            Gui.drawRect(- Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(name) / 2 - 3, -1.5F, (int) string_width + 8, -2, Color.green.getRGB());

                          //  fontobj.drawString(" §" + getHealthColor((int) e.getHealth()) + health, -8, +7, -1);
                           // fontobj.drawString(name, (int) -string_width, -3, 0);
                       //     font.drawString("" + getHealthColor((int) e.getHealth()) + health, -8, +7, -1);
                        	GlStateManager.resetColor();
            				GL11.glColor4f(1.0F, 1.0f, 1.0f, 1.0f);
                            Client.INSTANCE.unicodeBasicFontRenderer.drawString(name, (int) -string_width, + 0.5F, Color.white.getRGB());

//                            if (e.getCurrentArmor(0) != null && e.getCurrentArmor(0).getItem() instanceof ItemArmor) {
//                                this.renderItem(e.getCurrentArmor(0), 26, 1, 0);
//                            }
//                            if (e.getCurrentArmor(1) != null && e.getCurrentArmor(1).getItem() instanceof ItemArmor) {
//                                this.renderItem(e.getCurrentArmor(1), 13, 1, 0);
//                            }
//                            if (e.getCurrentArmor(2) != null && e.getCurrentArmor(2).getItem() instanceof ItemArmor) {
//                                this.renderItem(e.getCurrentArmor(2), 0, 1, 0);
//                            }
//                            if (e.getCurrentArmor(3) != null && e.getCurrentArmor(3).getItem() instanceof ItemArmor) {
//                                this.renderItem(e.getCurrentArmor(3), -13, 1, 0);
//                            }
//                            if (e.getHeldItem() != null) {
//                                this.renderItem(e.getHeldItem(), -26, 1, 0);
//                            }

                            GlStateManager.disableBlend();
                            GlStateManager.enableDepth();
                        	GlStateManager.resetColor();
            				GL11.glColor4f(1.0F, 1.0f, 1.0f, 1.0f);
                            GlStateManager.popMatrix();

                        }
                    }
                }
            }
            }

        }

    public static String removeColorCode(String text) {
        String finalText = text;
        if (finalText.contains("\u00a7")) {
            int i = 0;
            while (i < finalText.length()) {
                if (Character.toString(finalText.charAt(i)).equals("\u00a7")) {
                    try {
                        String part1 = finalText.substring(0, i);
                        String part2 = finalText.substring(Math.min((i + 2), finalText.length()));
                        finalText = part1 + part2;
                    }
                    catch (Exception part1) {
                    }
                }
                ++i;
            }
        }
        return finalText;
    }

    public void renderItem(ItemStack item, int xPos, int yPos, int zPos) {
        GL11.glPushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        GlStateManager.enableBlend();
        GL11.glBlendFunc((int)770, (int)771);
        GlStateManager.disableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        final IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(item);
        mc.getRenderItem().textureManager.bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.scale(16.0f, 16.0f, 0.0f);
        GL11.glTranslated(((float)xPos - 7.85f) / 16.0f, (float)(-5 + yPos) / 16.0f, (float)zPos / 16.0f);
        GlStateManager.rotate(180.0f,1.0f, 0.0f, (float)0.0f);

        if (ibakedmodel.isBuiltInRenderer()) {
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, -0.5f, -0.5f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            TileEntityItemStackRenderer.instance.renderByItem(item);
        } else {
            mc.getRenderItem().renderModel(ibakedmodel, -1, item);
        }
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }


    public String getHealthColor(final int hp) {

        if (hp > 15)
            return "a";
        if (hp > 10)
            return "e";
        if (hp > 5)
            return "6";
        if (hp < 2)
            return "4";
        return "c";

    }
}
