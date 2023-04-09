package tech.dort.dortware.impl.modules.render.hud;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.RenderHUDEvent;
import tech.dort.dortware.impl.managers.ModuleManager;
import tech.dort.dortware.impl.modules.render.Hud;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MemeTheme extends Theme {
    public MemeTheme(Module module) {
        super(module);
    }

    boolean yesnt;

    @Override
    public void render(RenderHUDEvent event) {
        if (mc.gameSettings.showDebugInfo)
            return;
        final Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        final Boolean playerModel = hud.playerModel.getValue();
        final Boolean watermark = hud.watermark.getValue();
        final Boolean armorHud = hud.armorHUD.getValue();
        final String alternativeNames = hud.alternativeNameMode.getValue().getDisplayName();
        int y = 3;
        Client.INSTANCE.getModuleManager().sort(mc.fontRendererObj);

        if (watermark) {
            GlStateManager.pushMatrix();
            mc.fontRendererObj.drawStringWithShadow(String.join(" ", "\247eMeme 1.0.0-SNAPSHOT"), 3, 3, -1);
            GlStateManager.popMatrix();
        }

        final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        final DecimalFormat bpsFormat = new DecimalFormat("#.##");
        mc.fontRendererObj.drawStringWithShadow(bpsFormat.format(xz) + " BPS", 3, 14, -1);

        final ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final ModuleManager moduleManager = Client.INSTANCE.getModuleManager();
        for (Module module : alternativeNames.equalsIgnoreCase("Memes") ? moduleManager.getModulesSortedAlternativeNoFont(mc.fontRendererObj) : alternativeNames.equalsIgnoreCase("Orialeng") ? moduleManager.getModulesSortedAlternativeDumbNoFont(mc.fontRendererObj) : moduleManager.getObjects()) {
            if (!module.isToggled())
                continue;
            ModuleData moduleData = module.getModuleData();
            String data = "";
            if (module.getSuffix()
                    != null) {
                data = module.getSuffix();
            }

            String name = moduleData.getName();
            if (alternativeNames.equalsIgnoreCase("Memes") && moduleData.hasOtherName())
                name = moduleData.getOtherName();
            else if (alternativeNames.equalsIgnoreCase("Orialeng") && moduleData.hasOtherNameDumb())
                name = moduleData.getOtherNameDumb();


            switch (hud.arraylistMode.getValue()) {
                case NORMAL:
                    yesnt = true;
                    break;
                case OLDMEME:
                    //might add for a color scheme
                    yesnt = false;
                    break;
            }
            float xPos = scaledResolution.getScaledWidth() - mc.fontRendererObj.getStringWidth((name + data)) - 3;
            if (yesnt) {
                mc.fontRendererObj.drawStringWithShadow(name + data, xPos, y, new Color(module.getModuleData().getName().hashCode()).getRGB());
            } else {
                //mc.fontRendererObj.drawRainbowString(name + data, xPos, y, 39, 2.5f);
                mc.fontRendererObj.drawStringWithShadow(name + data, xPos, y, ColorUtil.rainbow(y * 8, 2.5f));
            }
            y += 12;
        }
        int playerY = hud.tabGui.getValue() ? 210 : 90;
        int playerX = (scaledResolution.getScaledWidth() / 2) + 5;
        if (playerModel) {
            hud.drawEntityOnScreen(35, playerY, mc.thePlayer);
        }

        if (armorHud) {
            for (ItemStack itemStack : Lists.reverse(Arrays.asList(mc.thePlayer.inventory.armorInventory))) {
                if (itemStack != null) {
                    mc.fontRendererObj.drawStringWithShadow("\2472" + (itemStack.getMaxDamage() - itemStack.getItemDamage()), playerX - 3, scaledResolution.getScaledHeight() - 60, -1);
                    mc.getRenderItem().func_175042_a(itemStack, playerX, scaledResolution.getScaledHeight() - 52);
                }
                playerX += 22;
            }
        }
    }
}
