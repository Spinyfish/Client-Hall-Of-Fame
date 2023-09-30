package dev.tenacity.ui.mainmenu;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.ui.mainmenu.particles.ParticleEngine;
import dev.tenacity.utils.misc.*;
import dev.tenacity.utils.render.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {

    private ParticleEngine particleEngine;

    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Singleplayer"));
        add(new MenuButton("Multiplayer"));
        add(new MenuButton("Alt Manager"));
        add(new MenuButton("Settings"));
        add(new MenuButton("Exit"));
    }};

    private static boolean firstInit = false;

    @Override
    public void initGui() {
        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                Tenacity.INSTANCE.setDiscordRPC(new DiscordRPC());
            }
            firstInit = true;
        }

        if (particleEngine == null) {
            particleEngine = new ParticleEngine();
        }

        if (mc.gameSettings.guiScale != 2) {
            Tenacity.prevGuiScale = mc.gameSettings.guiScale;
            Tenacity.updateGuiScale = true;

            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }

        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        width = sr.getScaledWidth();
        height = sr.getScaledHeight();

        RoundedUtil.drawGradientCornerRL(
                0, 0,width, height, 0,
                HUDMod.getClientColors().getFirst().darker().darker().darker(),
                HUDMod.getClientColors().getSecond().darker().darker().darker()
        );

        RoundedUtil.drawGradientCornerRL(
                width / 2.0F - 120,
                height / 2.0F - 130,
                240, 275,
                5,
                new Color(0, 0, 0, 80),
                new Color(0, 0, 0, 80)
        );
      //  RenderUtil.drawImage(new ResourceLocation("Tenacity/idk.png"),0,0,990,550);
        float buttonWidth = 140;
        float buttonHeight = 25;

        for (int count = 0; count < buttons.size(); count += 1) {
            MenuButton button = buttons.get(count);

            button.x = width / 2f - buttonWidth / 2f;
            button.y = height / 2f - buttonHeight / 2f + (buttonHeight + 5) * count - 25;

            button.width = buttonWidth;
            button.height = buttonHeight;

            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "Alt Manager":
                        mc.displayGuiScreen(Tenacity.INSTANCE.getAltManager());
                        break;
                    case "Settings":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Exit":
                        mc.shutdown();
                        break;
                }
            };

            button.drawScreen(mouseX, mouseY);
        }

        lithiumBoldFont80.drawCenteredString("Lithium", width / 2f, height / 2f - 110, Color.WHITE.getRGB());
        lithiumFont18.drawCenteredString("by Liticane, Nyghtfull", width / 2f, height / 2f - 68, Color.WHITE.getRGB());

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onGuiClosed() {
        if (Tenacity.updateGuiScale) {
            mc.gameSettings.guiScale = Tenacity.prevGuiScale;
            Tenacity.updateGuiScale = false;
        }
    }

}
