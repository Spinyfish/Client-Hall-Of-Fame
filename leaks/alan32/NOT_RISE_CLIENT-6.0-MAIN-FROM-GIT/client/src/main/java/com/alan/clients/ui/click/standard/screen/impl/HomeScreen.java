package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2f;

import java.awt.*;

public final class HomeScreen extends Screen implements InstanceAccess {

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final RiseClickGUI clickGUI = getStandardClickGUI();

        /* Saves the position of the Rise logo */
        final Vector2f positionOfLogo = new Vector2f(clickGUI.position.x + 20, clickGUI.position.y + 20);

        /* Draws logo */
//        InstanceAccess.nunitoLarge.drawString(Rise.NAME, positionOfLogo.x, positionOfLogo.y, clickGUI.logoColor.hashCode());

        /* Draws version number */
//        InstanceAccess.nunitoMedium.drawString(Rise.VERSION,
//                positionOfLogo.x + InstanceAccess.nunitoLarge.width(Rise.NAME), positionOfLogo.y + 11,
//                clickGUI.fontDarkerColor.hashCode());

        /* Draws session information */
//        InstanceAccess.nunitoSmall.drawString("Username: " + PlayerUtil.name(),
//                positionOfLogo.x + 1.5f, positionOfLogo.y + 30, clickGUI.fontColor.hashCode());
//        InstanceAccess.nunitoSmall.drawString("UID: 1",
//                positionOfLogo.x + 1.5f, positionOfLogo.y + 43, clickGUI.fontColor.hashCode());

        final Vector2f positionOfSearch = new Vector2f(clickGUI.position.x + clickGUI.scale.x / 2, clickGUI.position.y + clickGUI.scale.y - 14);
        final String text = "Start typing to search...";
        this.nunitoNormal.drawString(text, positionOfSearch.x - this.nunitoNormal.width(text) / 2f, positionOfSearch.y, new Color(clickGUI.fontColor.getRed(), clickGUI.fontColor.getBlue(), clickGUI.fontColor.getGreen(), 70).hashCode());
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {

    }

    @Override
    public void onMouseRelease() {

    }

    @Override
    public void onBloom() {

    }

    @Override
    public void onInit() {

    }
}
