package optifine;

import java.io.InputStream;

public class OptiFineResourceLocator
{
    private static IOptiFineResourceLocator resourceLocator;

    public static void setResourceLocator(IOptiFineResourceLocator resourceLocator)
    {
        resourceLocator = resourceLocator;
        Class oclass = OptiFineResourceLocator.class;
        System.getProperties().put(oclass.getName() + ".class", oclass);
    }

    public static InputStream getOptiFineResourceStream(String name)
    {
        return resourceLocator == null ? null : resourceLocator.getOptiFineResourceStream(name);
    }
}
