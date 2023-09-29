package alos.stella.module.modules.visual;

import alos.stella.Stella;
import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.ui.draggble.DragModuleList;
import alos.stella.utils.ColorUtils;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.*;
import kotlin.text.StringsKt;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.VISUAL, array = false)
public class HUD extends Module {
    public final TextValue clientName = new TextValue("ClientName","Stella");
    public static final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom","Rainbow","Astolfo","Fade","Mixer"}, "Sky");
    private static final ListValue caseValue = new ListValue("Case", new String[]{"None", "Lower", "Upper"}, "None");
    private static final ListValue tagsStyleValue =  new ListValue("TagsStyle", new String[]{"-", "|", "()", "[]", "<>", "Default"}, "-");
    private final BoolValue waterMark = new BoolValue("WaterMark", true);
    private final BoolValue arraylist = new BoolValue("Arraylist", true);
    private final BoolValue tags = new BoolValue("Tags", true);
    private final BoolValue tagsArrayColor = new BoolValue("TagsArrayColor", false);
    private final BoolValue bps = new BoolValue("BPS", true);
    private final BoolValue fps = new BoolValue("FPS", true);
    private final BoolValue armor = new BoolValue("Armor", true);
    private final BoolValue health = new BoolValue("Health", true);
    private final IntegerValue colorRedValue = new IntegerValue("R", 238, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 130, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 238, 0, 255);

    private final FloatValue saturationValue = new FloatValue("saturation", 0.5f, 0f, 1f);
    private final FloatValue brightnessValue = new FloatValue("brightness", 1f, 0f, 1f);
    private final IntegerValue customY = new IntegerValue("CustomSpace", 10, 0, 20);
    private final IntegerValue secondValue = new IntegerValue("Second", 2, 1, 10);
    private final ListValue effectDisplay = new ListValue("Mode", new String[]{"Text", "Icon", "None"}, "Icon");

    private double lastDist;
    private int width;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));


    public class ModuleComparator implements Comparator<Module>{

        @Override
        public int compare(Module arg0, Module arg1) {
            if (mc.fontRendererObj.getStringWidth(getModName(arg0)) > mc.fontRendererObj.getStringWidth(getModName(arg1))){
                return -1;
            }
            if (mc.fontRendererObj.getStringWidth(getModName(arg0)) < mc.fontRendererObj.getStringWidth(getModName(arg1))){
                return 1;
            }
            return 0;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event){
        if (event.getEventState() == EventState.PRE) {
            EntityPlayerSP player = mc.thePlayer;

            double xDist = player.posX - player.lastTickPosX;
            double zDist = player.posZ - player.lastTickPosZ;

            lastDist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    public void drawHud(ScaledResolution sr) {

        Collections.sort(Stella.moduleManager.getModules(), new ModuleComparator());

        DragModuleList di = (DragModuleList) Stella.draggableHUD.getDraggableComponentByClass(DragModuleList.class);
        di.setWidth(145);
        di.setHeight(100);
        float displayWidth = di.getX();
        float y = di.getY();

        if (waterMark.get() && getState()) {
            mc.fontRendererObj.drawStringWithShadow(clientName.get(), 3, 3, getColor(100).getRGB());
        }
        if (arraylist.get() && getState()) {
            int count = 0;
            int counter =0;
            for (Module module : Stella.moduleManager.getModules()) {
                if (!module.getState())
                    continue;

                double offset = count * (mc.fontRendererObj.FONT_HEIGHT + customY.get());

                mc.fontRendererObj.drawStringWithShadow(getModName(module), displayWidth + 140.5F - mc.fontRendererObj.getStringWidth(getModName(module)) - 4F, (float) (y + offset), getColor(counter).getRGB());

                counter++;
                count++;

            }
        }
        // 8*60
        if (fps.get() && getState()) {
            String slash = "";
            final String fps = "FPS: " + Minecraft.getDebugFPS();
            if (bps.get()) {
                slash = " | ";
            }

            mc.fontRendererObj.drawStringWithShadow(fps + slash, 3, 8 * 61.5f - (mc.currentScreen instanceof GuiChat ? 10 : 0), getColor(100).getRGB());
        }
        if (bps.get() && getState()) {
            mc.fontRendererObj.drawStringWithShadow(
                    String.format("%.2f blocks/s", lastDist * 20 * mc.timer.timerSpeed),
                    fps.get() ? 55 : 3,
                    8 * 61.5f - (mc.currentScreen instanceof GuiChat ? 10 : 0),
                    getColor(100).getRGB());
        }
        if (effectDisplay.get().equalsIgnoreCase("Text") && getState()) {
            int x;
            x = 0;
            for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String PType = I18n.format(potion.getName());
                String d2 = "";
                switch (effect.getAmplifier()) {
                    case 1: {
                        PType = (Color.white.getRGB()) + " II";
                        break;
                    }
                    case 2: {
                        PType = (Color.white.getRGB()) + " III";
                        break;
                    }
                    case 3: {
                        PType = (Color.white.getRGB()) + " IV";
                        break;
                    }
                    case 4: {
                        PType = (Color.white.getRGB()) + " V";
                        break;
                    }
                    case 5: {
                        PType = (Color.white.getRGB()) + " VI";
                        break;
                    }
                    case 6: {
                        PType = (Color.white.getRGB()) + "VII";
                        break;
                    }
                    case 7: {
                        PType = (Color.white.getRGB()) + " VIII";
                        break;
                    }
                }
                if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                    d2 = (EnumChatFormatting.YELLOW) + Potion.getDurationString(effect);
                } else if (effect.getDuration() < 300) {
                    d2 = (EnumChatFormatting.RED) + Potion.getDurationString(effect);
                } else if (effect.getDuration() > 600) {
                    d2 = (EnumChatFormatting.WHITE) + Potion.getDurationString(effect);
                }
                int x1 = (int) ((double) (sr.getScaledWidth() - 6) * 1.33);
                int y1 = (int) ((float) (sr.getScaledHeight() - 32 - mc.fontRendererObj.FONT_HEIGHT + x + 5) * 1.33f);
                if (potion.hasStatusIcon()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    int var10 = potion.getStatusIconIndex();
                    ResourceLocation location = new ResourceLocation("textures/gui/container/inventory.png");
                    mc.getTextureManager().bindTexture(location);
                    GlStateManager.scale(0.75, 0.75, 0.75);
                    mc.ingameGUI.drawTexturedModalRect(x1 - 9, y1 + 20, var10 % 8 * 18, 198 + var10 / 8 * 18, 18, 18);
                    GlStateManager.popMatrix();
                }
                int y2 = sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT + x - 5;
                int m2 = 15;
                mc.fontRendererObj.drawStringWithShadow(PType, sr.getScaledWidth() - m2 - mc.fontRendererObj.getStringWidth(PType) - 1, y2 - mc.fontRendererObj.FONT_HEIGHT - 1, potion.getLiquidColor());
                mc.fontRendererObj.drawStringWithShadow(d2, sr.getScaledWidth() - m2 - mc.fontRendererObj.getStringWidth(d2) - 1, y2, -1);
                x -= 17;
            }
        }
        if (health.get()  && getState()) {
            if (mc.thePlayer.getHealth() >= 0.0F &&
                    mc.thePlayer.getHealth() < 10.0F)
                this.width = 3;
            if (mc.thePlayer.getHealth() >= 10.0F &&
                    mc.thePlayer.getHealth() < 100.0F)
                this.width = 3;
            float health = mc.thePlayer.getHealth();
            float absorptionHealth = mc.thePlayer.getAbsorptionAmount();
            String absorp = (absorptionHealth <= 0.0F) ? "" : ("\u00a7e" + " " + this.decimalFormat.format((absorptionHealth / 2.0F)) + "\u00a76\u2764");
            String string = this.decimalFormat.format((health / 2.0F)) + "\u00a7c\u2764" + absorp;
            int x = (new ScaledResolution(mc)).getScaledWidth() / 2 - this.width - 1;
            int y1 = (new ScaledResolution(mc)).getScaledHeight() / 2 + 25;
            mc.fontRendererObj.drawString(string, (absorptionHealth > 0.0F) ? (x - 15.5F) : (x - 3.5F), y1, getHealthColor(), true);
            GL11.glPushAttrib(1048575);
            GL11.glPushMatrix();
            float i = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(Gui.icons);
            while (i < mc.thePlayer.getMaxHealth() / 2.0F) {
                Gui.drawTexturedModalRect(sr.getScaledWidth() / 2.0F - mc.thePlayer.getMaxHealth() / 2.5F * 10.0F / 2.0F + i * 8.0F, sr
                        .getScaledHeight() / 2.0F + 15.0F, 16, 0, 9, 9);
                i++;
            }
            i = 0.0F;
            while (i < mc.thePlayer.getHealth() / 2.0F) {
                Gui.drawTexturedModalRect(sr.getScaledWidth() / 2.0F - mc.thePlayer.getMaxHealth() / 2.5F * 10.0F / 2.0F + i * 8.0F, sr
                        .getScaledHeight() / 2.0F + 15.0F, 52, 0, 9, 9);
                i++;
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
        if (armor.get() && getState()) {
            GL11.glPushMatrix();
            List<ItemStack> stuff = new ArrayList<>();
            boolean onWater = (mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water));
            int split = -3;
            for (int index = 3; index >= 0; index--) {
                ItemStack armer = mc.thePlayer.inventory.armorInventory[index];
                if (armer != null)
                    stuff.add(armer);
            }
            if (mc.thePlayer.getCurrentEquippedItem() != null)
                stuff.add(mc.thePlayer.getCurrentEquippedItem());
            for (ItemStack everything : stuff) {
                if (mc.theWorld != null) {
                    RenderHelper.enableGUIStandardItemLighting();
                    split += 16;
                }
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                GlStateManager.enableBlend();
                (mc.getRenderItem()).zLevel = -150.0F;
                mc.getRenderItem().renderItemIntoGUI(everything, split + sr.getScaledWidth() / 2 - 4, sr.getScaledHeight() - (onWater ? 65 : 53) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : -2));
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + sr.getScaledWidth() / 2 - 4, sr.getScaledHeight() - (onWater ? 65 : 53) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : -2));
                DrawUtils.renderEnchantText(everything, split + sr.getScaledWidth() / 2 - 3, sr.getScaledHeight() * 2 - (onWater ? 145 : 115) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : -13));
                (mc.getRenderItem()).zLevel = 0.0F;
                GlStateManager.disableBlend();
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.enableDepth();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
                everything.getEnchantmentTagList();
            }
            GL11.glPopMatrix();
        }
    }

    public Color getColor(final int index) {
        switch (colorModeValue.get()) {
            case "Custom":
                return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
            case "Rainbow":
                return new Color(alos.stella.utils.render.ColorUtils.getRainbowOpaque(secondValue.get(), saturationValue.get(), brightnessValue.get(), index));
            case "Astolfo":
                return alos.stella.utils.render.ColorUtils.skyRainbow(index, saturationValue.get(), brightnessValue.get());
            default:
                return ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), index, 100);
        }
    }

    private final String getModTag(Module m) {
        if ((Boolean)this.tags.get() && m.getTag() != null) {
            String returnTag = ' ' + ((Boolean)this.tagsArrayColor.get() ? "" : EnumChatFormatting.GRAY.toString());
            if (!StringsKt.equals(this.tagsStyleValue.get(), "default", true)) {
                returnTag = returnTag + ((this.tagsStyleValue.get()).charAt(0)) + (!StringsKt.equals(this.tagsStyleValue.get(), "-", true) && !StringsKt.equals(this.tagsStyleValue.get(), "|", true) ? "" : " ");
            }

            returnTag = returnTag + m.getTag();
            if (!StringsKt.equals(this.tagsStyleValue.get(), "default", true) && !StringsKt.equals(this.tagsStyleValue.get(), "-", true) && !StringsKt.equals(this.tagsStyleValue.get(), "|", true)) {
                returnTag = returnTag + ((this.tagsStyleValue.get()).charAt(1));
            }

            return returnTag;
        } else {
            return "";
        }
    }

    @NotNull
    public final String getModName(@NotNull Module mod) {
        String displayName = (mod.getName()) + getModTag(mod);
        switch (caseValue.get()) {
            case "lower":
                displayName = displayName.toLowerCase();
                break;
            case "upper":
                displayName = displayName.toUpperCase();
        }

        return displayName;
    }
    private int getHealthColor() {
        if (mc.thePlayer.getHealth() <= 2.0F)
            return (new Color(255, 0, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 6.0F)
            return (new Color(255, 110, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 8.0F)
            return (new Color(255, 182, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 10.0F)
            return (new Color(255, 255, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 13.0F)
            return (new Color(255, 255, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 15.5F)
            return (new Color(182, 255, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 18.0F)
            return (new Color(108, 255, 0)).getRGB();
        if (mc.thePlayer.getHealth() <= 20.0F)
            return (new Color(0, 255, 0)).getRGB();
        return 0;
    }
}
