package net.minecraft.src;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;

public class ResourceUtils
{
    private static ReflectorClass ForgeAbstractResourcePack = new ReflectorClass(AbstractResourcePack.class);
    private static ReflectorField ForgeAbstractResourcePack_resourcePackFile = new ReflectorField(ForgeAbstractResourcePack, "resourcePackFile");
    private static boolean directResourcePack = true;
    private static Map localeProperties = null;

    public static File getResourcePackFile(AbstractResourcePack arp)
    {
        if (directResourcePack)
        {
            try
            {
                return arp.resourcePackFile; //made public
            }
            catch (IllegalAccessError var2)
            {
                directResourcePack = false;

                if (!ForgeAbstractResourcePack_resourcePackFile.exists())
                {
                    throw var2;
                }
            }
        }

        return (File)Reflector.getFieldValue(arp, ForgeAbstractResourcePack_resourcePackFile);
    }

    public static Map getLocaleProperties()
    {
        if (localeProperties == null)
        {
            try
            {
                Field e = Reflector.getField(I18n.class, Locale.class);
                Object locale = e.get((Object)null);
                Field fieldProperties = Reflector.getField(Locale.class, Map.class);
                localeProperties = (Map)fieldProperties.get(locale);
            }
            catch (Throwable var3)
            {
                Config.warn("[ResourceUtils] Error getting locale properties: " + var3.getClass().getName() + ": " + var3.getMessage());
                localeProperties = new HashMap();
            }
        }

        return localeProperties;
    }
}
