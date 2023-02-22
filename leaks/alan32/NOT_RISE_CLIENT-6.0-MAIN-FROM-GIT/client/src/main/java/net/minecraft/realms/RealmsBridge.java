package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;

public class RealmsBridge extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private GuiScreen previousScreen;

    public void switchToRealms(final GuiScreen p_switchToRealms_1_) {
        this.previousScreen = p_switchToRealms_1_;

        try {
            final Class<?> oclass = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
            final Constructor<?> constructor = oclass.getDeclaredConstructor(RealmsScreen.class);
            constructor.setAccessible(true);
            final Object object = constructor.newInstance(this);
            Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen) object).getProxy());
        } catch (final Exception exception) {
            LOGGER.error("Realms module missing", exception);
        }
    }

    public GuiScreenRealmsProxy getNotificationScreen(final GuiScreen p_getNotificationScreen_1_) {
        try {
            this.previousScreen = p_getNotificationScreen_1_;
            final Class<?> oclass = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
            final Constructor<?> constructor = oclass.getDeclaredConstructor(RealmsScreen.class);
            constructor.setAccessible(true);
            final Object object = constructor.newInstance(this);
            return ((RealmsScreen) object).getProxy();
        } catch (final Exception exception) {
            LOGGER.error("Realms module missing", exception);
            return null;
        }
    }

    public void init() {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
