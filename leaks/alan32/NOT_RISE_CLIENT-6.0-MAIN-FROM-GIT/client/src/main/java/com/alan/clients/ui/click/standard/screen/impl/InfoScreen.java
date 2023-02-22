package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.ScissorUtil;
import com.alan.clients.util.vector.Vector2f;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author Hazsi
 * @since 10/31/22
 */
public class InfoScreen extends Screen {

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {

        final Vector2f position = getStandardClickGUI().getPosition();
        final Vector2f scale = getStandardClickGUI().getScale();
        final double sidebar = getStandardClickGUI().getSidebar().sidebarWidth;

        // Draw left column (client name and version)
        FontManager.getProductSansRegular(32).drawString(Client.NAME, position.getX() + sidebar + 20,
                position.getY() + 20, Color.WHITE.getRGB());
        FontManager.getProductSansRegular(16).drawString(Client.VERSION,
                position.getX() + sidebar + 20 + FontManager.getProductSansRegular(32).width(Client.NAME),
                position.getY() + 18, new Color(255, 255, 255, 100).getRGB());
        FontManager.getNunito(17).drawString(Client.VERSION_FULL, position.getX() + sidebar + 20,
                position.getY() + 50, new Color(255, 255, 255, 164).getRGB());
        FontManager.getNunito(17).drawString(Client.VERSION_DATE, position.getX() + sidebar + 20,
                position.getY() + 65, new Color(255, 255, 255, 164).getRGB());

        // Draw right column (user info)

        // Draw credits
        final int seconds = 45;
        final double offset = ((System.currentTimeMillis() / 1000D) % seconds) * 11;

        ScissorUtil.enable();
        ScissorUtil.scissor(new ScaledResolution(mc), position.getX() + sidebar, position.getY() + 90, scale.getX(), scale.getY() - 175);

        // Draw text
        FontManager.getNunito(17).drawString(getCredits1(), position.getX() + sidebar + 20,
                position.getY() + 100 - offset + (scale.getY() - 175), new Color(164, 164, 164, 64).getRGB());
        FontManager.getNunito(17).drawString(getCredits2(), position.getX() + sidebar + 155,
                position.getY() + 100 - offset + (scale.getY() - 175), new Color(164, 164, 164, 64).getRGB());

        // Draw gradients to make the fade in/out look on the scrolling text
        RenderUtil.verticalGradient(position.getX() + sidebar, position.getY() + 89, scale.getX() - sidebar,
                35, getStandardClickGUI().getBackgroundColor(), ColorUtil.withAlpha(getStandardClickGUI().getBackgroundColor(), 0));
        RenderUtil.verticalGradient(position.getX() + sidebar, position.getY() + scale.getY() - 120,
                scale.getX() - sidebar, 35, ColorUtil.withAlpha(getStandardClickGUI().getBackgroundColor(), 0),
                getStandardClickGUI().getBackgroundColor());

        ScissorUtil.disable();
    }

    // Returns the first column of the credits.
    // Method so I can hotswap the string
    private static String getCredits1() {
        return "Rise " + Client.VERSION + " (riseclient.com)\n" +
                "\n" +
                "Designed and built by Alan and Hazsi.\n" +
                "\n" +
                "Additional Development\n" +
                "  -> Nicklas, Tecnio, Patrick, Strikeless\n" +
                "\n" +
                "UI/UX Design\n" +
                "  -> Hazsi, Alan\n" +
                "\n" +
                "Rendering/OpenGL\n" +
                "  -> Patrick, Strikeless\n" +
                "\n" +
                "Protection and Obfuscation\n" +
                "  -> Alan, NyanCatForEver\n" +
                "\n" +
                "Scripting Documentation\n" +
                "  -> Alice\n" +
                "\n" +
                "Localization\n" +
                "-> a cat (Romanian)\n" +
                "-> Alice (Russian)\n" +
                "-> Bx2 (Arabic)\n" +
                "-> El Gatito Grande (Portuguese)\n" +
                "-> days (Hebrew)\n" +
                "-> Duits/Jess (Indonesian)\n" +
                "-> fan87 (Chinese Traditional)\n" +
                "-> Grekgamer13 (Greek)\n" +
                "-> Jb (Austrian)\n" +
                "-> kinja (Polish)\n" +
                "-> I_only_die_twice (Swedish)\n" +
                "\n" +
                "Special Thanks\n" +
                "  -> Auth and Error as contributing to development\n" +
                "  -> Config and script makers\n" +
                "  -> You, as a user of Rise. Thank you, on behalf of all of us!\n";
    }

    // Returns the second column of the credits
    private static String getCredits2() {
        return "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "-> im ray (Latvian)\n" +
                "-> Novus (Spanish)\n" +
                "-> Mongrall (Hungarian)\n" +
                "-> MOON (Italian)\n" +
                "-> Stimular (Czech)\n" +
                "-> Tapludeforfair (French)\n" +
                "-> toastedwaffles (Vietnamese)\n" +
                "-> trollo (German)\n" +
                "-> Velcola (Norwegian)\n" +
                "-> whoistinywifi (Thai)\n" +
                "-> YK_FCZ (Chinese Simplified)\n";
    }
}
