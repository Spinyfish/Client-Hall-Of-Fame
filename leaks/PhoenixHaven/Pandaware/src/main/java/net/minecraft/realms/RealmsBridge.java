package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import org.tinylog.Logger;

public class RealmsBridge extends RealmsScreen
{
    private GuiScreen previousScreen;

    public void switchToRealms(GuiScreen p_switchToRealms_1_)
    {
        this.previousScreen = p_switchToRealms_1_;

        try
        {
            Class<?> oclass = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
            Constructor<?> constructor = oclass.getDeclaredConstructor(new Class[] {RealmsScreen.class});
            constructor.setAccessible(true);
            Object object = constructor.newInstance(new Object[] {this});
            Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)object).getProxy());
        }
        catch (Exception exception)
        {
            Logger.error((String)"Realms module missing", (Throwable)exception);
        }
    }

    public GuiScreenRealmsProxy getNotificationScreen(GuiScreen p_getNotificationScreen_1_)
    {
        try
        {
            this.previousScreen = p_getNotificationScreen_1_;
            Class<?> oclass = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
            Constructor<?> constructor = oclass.getDeclaredConstructor(new Class[] {RealmsScreen.class});
            constructor.setAccessible(true);
            Object object = constructor.newInstance(new Object[] {this});
            return ((RealmsScreen)object).getProxy();
        }
        catch (Exception exception)
        {
            Logger.error((String)"Realms module missing", (Throwable)exception);
            return null;
        }
    }

    public void init()
    {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
