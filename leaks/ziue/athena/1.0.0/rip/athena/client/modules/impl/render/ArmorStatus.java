package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.renderer.entity.*;

public class ArmorStatus extends Module
{
    @ConfigValue.Boolean(name = "Item Name")
    private boolean armorstatusitemname;
    @ConfigValue.Boolean(name = "Item Equipped")
    private boolean armorstatusitemequipped;
    @ConfigValue.Boolean(name = "Preset Durability Colors")
    private boolean duraColors;
    @ConfigValue.Color(name = "Durability Color")
    private Color valueColor;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.List(name = "Direction", values = { "Vertical", "Horizontal" })
    private String armourstatusdirection;
    @ConfigValue.List(name = "Value Display", values = { "Percent", "Value Damage/Max", "Value Damage" })
    private String armorstatusdisplay;
    @ConfigValue.List(name = "Value Left/Right", values = { "Left", "Right" })
    private String leftRight;
    private HUDElement hud;
    private int armorstatuswidth;
    private int armorstatusheight;
    
    public ArmorStatus() {
        super("Armor Status", Category.RENDER, "Athena/gui/mods/armorstatus.png");
        this.armorstatusitemname = false;
        this.armorstatusitemequipped = true;
        this.duraColors = true;
        this.valueColor = Color.WHITE;
        this.customFont = true;
        this.armourstatusdirection = "Vertical";
        this.armorstatusdisplay = "Value Damage";
        this.leftRight = "Left";
        this.armorstatuswidth = 10;
        this.armorstatusheight = 10;
        (this.hud = new HUDElement("armorstatus", this.armorstatuswidth, this.armorstatusheight) {
            @Override
            public void onRender() {
                ArmorStatus.this.render();
            }
        }).setX(0);
        this.hud.setY(200);
        this.addHUD(this.hud);
    }
    
    private EnumChatFormatting getColorThreshold(final int percent) {
        if (percent > 75 && percent <= 100) {
            return EnumChatFormatting.GREEN;
        }
        if (percent > 50 && percent <= 75) {
            return EnumChatFormatting.YELLOW;
        }
        if (percent > 25 && percent <= 50) {
            return EnumChatFormatting.RED;
        }
        if (percent >= 0 && percent <= 25) {
            return EnumChatFormatting.DARK_RED;
        }
        return EnumChatFormatting.WHITE;
    }
    
    public void render() {
        if (this.armourstatusdirection.contains("Horizontal")) {
            this.armorstatuswidth = 170;
            if (this.armorstatusdisplay.contains("Value Damage/Max")) {
                this.armorstatuswidth += 80;
                if (this.armorstatusitemequipped) {
                    this.armorstatuswidth += 35;
                }
            }
            if (this.armorstatusdisplay.equals("Percent")) {
                this.armorstatuswidth += 20;
            }
            if (this.armorstatusitemequipped) {
                this.armorstatuswidth += 35;
            }
            this.armorstatusheight = 15;
        }
        else {
            this.armorstatuswidth = 50;
            this.armorstatusheight = 65;
            if (this.armorstatusitemequipped) {
                this.armorstatusheight += 20;
            }
            if (this.armorstatusitemname) {
                this.armorstatuswidth += 100;
            }
            if (this.armorstatusdisplay.contains("Value Damage/Max")) {
                this.armorstatuswidth += 20;
            }
        }
        this.hud.setHeight(this.armorstatusheight);
        this.hud.setWidth(this.armorstatuswidth);
        if (ArmorStatus.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        final int posX = this.hud.getX();
        final int posY = this.hud.getY();
        int xdraw = this.armorstatusheight - 17;
        if (this.armourstatusdirection.contains("Vertical")) {
            final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
            final ItemStack[] reversed = new ItemStack[4];
            for (int i = 0; i < ArmorStatus.mc.thePlayer.inventory.armorInventory.length; ++i) {
                final ItemStack itemStack = ArmorStatus.mc.thePlayer.inventory.armorInventory[i];
                if (itemStack != null) {
                    reversed[3 - i] = itemStack;
                }
            }
            if (this.armorstatusitemequipped && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null) {
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                final ItemStack itemStack2 = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
                final int maxDamage = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getMaxDamage();
                final int damage = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItemDamage();
                final int percent = (maxDamage > 0) ? (100 - damage * 100 / maxDamage) : 1000;
                String toDisplay = (maxDamage > 0) ? (this.armorstatusdisplay.equalsIgnoreCase("Percent") ? (percent + "% ") : (this.armorstatusdisplay.equalsIgnoreCase("Value Damage/Max") ? (maxDamage - damage + "/" + maxDamage) : ("" + (maxDamage - damage)))) : "";
                String color = EnumChatFormatting.WHITE.toString();
                if (this.armorstatusitemname) {
                    toDisplay = toDisplay + " " + EnumChatFormatting.WHITE.toString() + itemStack2.getDisplayName();
                }
                if (this.duraColors) {
                    color = this.getColorThreshold(percent).toString();
                }
                else {
                    color = "";
                }
                int width = ArmorStatus.mc.fontRendererObj.getStringWidth(toDisplay);
                if (!this.customFont) {
                    ArmorStatus.mc.fontRendererObj.drawStringWithShadow(color + toDisplay, (float)(posX + (this.leftRight.equalsIgnoreCase("Left") ? ((maxDamage > 0) ? (this.armorstatuswidth - width - 21) : (this.armorstatuswidth - width - 21)) : ((maxDamage > 0) ? 20 : 16))), (float)(xdraw + posY + 5), this.valueColor.getRGB());
                }
                else {
                    width = FontManager.getProductSansRegular(30).width(EnumChatFormatting.getTextWithoutFormattingCodes(toDisplay));
                    FontManager.getProductSansRegular(30).drawString(EnumChatFormatting.getTextWithoutFormattingCodes(toDisplay), posX - 3 + (this.leftRight.equalsIgnoreCase("Left") ? ((maxDamage > 0) ? (this.armorstatuswidth - width - 21) : (this.armorstatuswidth - width - 21)) : ((maxDamage > 0) ? 20 : 16)), xdraw + posY + 1, this.valueColor.getRGB());
                }
                itemRenderer.renderItemAndEffectIntoGUI(itemStack2, this.leftRight.equalsIgnoreCase("Left") ? (posX + this.armorstatuswidth - 20) : posX, xdraw + posY + 0);
                itemRenderer.renderItemOverlayIntoGUI(ArmorStatus.mc.fontRendererObj, itemStack2, this.leftRight.equalsIgnoreCase("Left") ? (posX + this.armorstatuswidth - 20) : posX, xdraw + posY + 2, null);
                xdraw -= 17;
            }
            for (final ItemStack itemStack3 : ArmorStatus.mc.thePlayer.inventory.armorInventory) {
                if (itemStack3 != null) {
                    GlStateManager.disableLighting();
                    GlStateManager.enableBlend();
                    final int maxDamage2 = itemStack3.getMaxDamage();
                    final int damage2 = itemStack3.getItemDamage();
                    final int percent2 = (maxDamage2 > 0) ? (100 - damage2 * 100 / maxDamage2) : 1000;
                    String toDisplay2 = this.armorstatusdisplay.equalsIgnoreCase("Percent") ? (percent2 + "% ") : (this.armorstatusdisplay.equalsIgnoreCase("Value Damage/Max") ? (maxDamage2 - damage2 + "/" + maxDamage2) : ("" + (maxDamage2 - damage2)));
                    String color2 = EnumChatFormatting.WHITE.toString();
                    if (this.armorstatusitemname) {
                        toDisplay2 = toDisplay2 + " " + EnumChatFormatting.WHITE.toString() + itemStack3.getDisplayName();
                    }
                    if (this.duraColors) {
                        color2 = this.getColorThreshold(percent2).toString();
                    }
                    else {
                        color2 = "";
                    }
                    int width2 = ArmorStatus.mc.fontRendererObj.getStringWidth(toDisplay2);
                    if (!this.customFont) {
                        ArmorStatus.mc.fontRendererObj.drawStringWithShadow(color2 + toDisplay2, (float)(posX + (this.leftRight.equalsIgnoreCase("Left") ? (this.armorstatuswidth - width2 - 21) : 20)), (float)(xdraw + posY + 5), this.valueColor.getRGB());
                    }
                    else {
                        width2 = FontManager.getProductSansRegular(30).width(EnumChatFormatting.getTextWithoutFormattingCodes(toDisplay2));
                        FontManager.getProductSansRegular(30).drawString(EnumChatFormatting.getTextWithoutFormattingCodes(toDisplay2), posX - 3 + (this.leftRight.equalsIgnoreCase("Left") ? (this.armorstatuswidth - width2 - 21) : 20), xdraw + posY + 1, this.valueColor.getRGB());
                    }
                    itemRenderer.renderItemAndEffectIntoGUI(itemStack3, this.leftRight.equalsIgnoreCase("Left") ? (posX + this.armorstatuswidth - 20) : posX, xdraw + posY + 0);
                    itemRenderer.renderItemOverlayIntoGUI(ArmorStatus.mc.fontRendererObj, itemStack3, this.leftRight.equalsIgnoreCase("Left") ? (posX + this.armorstatuswidth - 20) : posX, xdraw + posY + 2, null);
                    xdraw -= 17;
                }
            }
        }
        int ydraw = 0;
        if (this.armourstatusdirection.contains("Horizontal")) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final RenderItem itemRenderer2 = Minecraft.getMinecraft().getRenderItem();
            final ItemStack[] reversed2 = new ItemStack[4];
            for (int j = 0; j < ArmorStatus.mc.thePlayer.inventory.armorInventory.length; ++j) {
                final ItemStack itemStack4 = ArmorStatus.mc.thePlayer.inventory.armorInventory[j];
                if (itemStack4 != null) {
                    reversed2[3 - j] = itemStack4;
                }
            }
            for (final ItemStack itemStack5 : reversed2) {
                if (itemStack5 != null) {
                    GlStateManager.disableLighting();
                    GlStateManager.enableBlend();
                    final int maxDamage3 = itemStack5.getMaxDamage();
                    final int damage3 = itemStack5.getItemDamage();
                    final int percent3 = (maxDamage3 > 0) ? (100 - damage3 * 100 / maxDamage3) : 1000;
                    final String toDraw = this.armorstatusdisplay.equalsIgnoreCase("Percent") ? (percent3 + "%") : (this.armorstatusdisplay.equalsIgnoreCase("Value Damage/Max") ? ("" + (maxDamage3 - damage3) + "/" + maxDamage3) : ("" + (maxDamage3 - damage3)));
                    String color3 = EnumChatFormatting.WHITE.toString();
                    int width3 = ArmorStatus.mc.fontRendererObj.getStringWidth(toDraw);
                    if (this.duraColors) {
                        color3 = this.getColorThreshold(percent3).toString();
                    }
                    else {
                        color3 = "";
                    }
                    if (this.customFont) {
                        width3 = FontManager.getProductSansRegular(30).width(EnumChatFormatting.getTextWithoutFormattingCodes(toDraw));
                        FontManager.getProductSansRegular(30).drawString(EnumChatFormatting.getTextWithoutFormattingCodes(toDraw), posX + ydraw + 16, posY + 1, this.valueColor.getRGB());
                    }
                    else {
                        ArmorStatus.mc.fontRendererObj.drawStringWithShadow(color3 + toDraw, (float)(posX + ydraw + 18), (float)(posY + 5), this.valueColor.getRGB());
                    }
                    itemRenderer2.renderItemAndEffectIntoGUI(itemStack5, posX + ydraw + 0, posY + 0);
                    itemRenderer2.renderItemOverlayIntoGUI(ArmorStatus.mc.fontRendererObj, itemStack5, posX + ydraw + 0, posY + 0, "");
                    ydraw += width3 + 20;
                }
            }
            if (this.armorstatusitemequipped && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null) {
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                final ItemStack itemStack = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
                final int maxDamage4 = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getMaxDamage();
                final int damage4 = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItemDamage();
                final int percent4 = (maxDamage4 > 0) ? (100 - damage4 * 100 / maxDamage4) : 1000;
                final String toDraw2 = (maxDamage4 > 0) ? (this.armorstatusdisplay.equalsIgnoreCase("Percent") ? (percent4 + "% ") : (this.armorstatusdisplay.equalsIgnoreCase("Value Damage/Max") ? (maxDamage4 - damage4 + "/" + maxDamage4) : ("" + (maxDamage4 - damage4)))) : "";
                String color4 = EnumChatFormatting.WHITE.toString();
                int width4 = ArmorStatus.mc.fontRendererObj.getStringWidth(toDraw2);
                if (this.duraColors) {
                    color4 = this.getColorThreshold(percent4).toString();
                }
                else {
                    color4 = "";
                }
                if (this.customFont) {
                    width4 = FontManager.getProductSansRegular(30).width(EnumChatFormatting.getTextWithoutFormattingCodes(toDraw2));
                    FontManager.getProductSansRegular(30).drawString(EnumChatFormatting.getTextWithoutFormattingCodes(toDraw2), posX + ydraw + 15, posY + 1, this.valueColor.getRGB());
                }
                else {
                    ArmorStatus.mc.fontRendererObj.drawStringWithShadow(color4 + toDraw2, (float)(posX + ydraw + 18), (float)(posY + 5), this.valueColor.getRGB());
                }
                itemRenderer2.renderItemAndEffectIntoGUI(itemStack, posX + ydraw, posY);
                itemRenderer2.renderItemOverlayIntoGUI(ArmorStatus.mc.fontRendererObj, itemStack, posX + ydraw + 0, posY + 0, "");
                ydraw += width4 + 20;
            }
        }
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
}
