package optifine;

import com.google.common.base.*;
import java.util.regex.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import java.io.*;
import org.apache.commons.io.*;
import com.google.common.collect.*;
import java.util.*;

public class Lang
{
    private static final Splitter splitter;
    private static final Pattern pattern;
    
    public static void resourcesReloaded() {
        final Map map = I18n.getLocaleProperties();
        final List<String> list = new ArrayList<String>();
        final String s = "optifine/lang/";
        final String s2 = "en_US";
        final String s3 = ".lang";
        list.add(s + s2 + s3);
        if (!Config.getGameSettings().language.equals(s2)) {
            list.add(s + Config.getGameSettings().language + s3);
        }
        final String[] astring = list.toArray(new String[list.size()]);
        loadResources(Config.getDefaultResourcePack(), astring, map);
        final IResourcePack[] airesourcepack = Config.getResourcePacks();
        for (int i = 0; i < airesourcepack.length; ++i) {
            final IResourcePack iresourcepack = airesourcepack[i];
            loadResources(iresourcepack, astring, map);
        }
    }
    
    private static void loadResources(final IResourcePack p_loadResources_0_, final String[] p_loadResources_1_, final Map p_loadResources_2_) {
        try {
            for (int i = 0; i < p_loadResources_1_.length; ++i) {
                final String s = p_loadResources_1_[i];
                final ResourceLocation resourcelocation = new ResourceLocation(s);
                if (p_loadResources_0_.resourceExists(resourcelocation)) {
                    final InputStream inputstream = p_loadResources_0_.getInputStream(resourcelocation);
                    if (inputstream != null) {
                        loadLocaleData(inputstream, p_loadResources_2_);
                    }
                }
            }
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }
    
    public static void loadLocaleData(final InputStream p_loadLocaleData_0_, final Map p_loadLocaleData_1_) throws IOException {
        for (final String s : IOUtils.readLines(p_loadLocaleData_0_, Charsets.UTF_8)) {
            if (!s.isEmpty() && s.charAt(0) != '#') {
                final String[] astring = (String[])Iterables.toArray(Lang.splitter.split((CharSequence)s), (Class)String.class);
                if (astring == null || astring.length != 2) {
                    continue;
                }
                final String s2 = astring[0];
                final String s3 = Lang.pattern.matcher(astring[1]).replaceAll("%$1s");
                p_loadLocaleData_1_.put(s2, s3);
            }
        }
    }
    
    public static String get(final String p_get_0_) {
        return I18n.format(p_get_0_, new Object[0]);
    }
    
    public static String get(final String p_get_0_, final String p_get_1_) {
        final String s = I18n.format(p_get_0_, new Object[0]);
        return (s != null && !s.equals(p_get_0_)) ? s : p_get_1_;
    }
    
    public static String getOn() {
        return I18n.format("options.on", new Object[0]);
    }
    
    public static String getOff() {
        return I18n.format("options.off", new Object[0]);
    }
    
    public static String getFast() {
        return I18n.format("options.graphics.fast", new Object[0]);
    }
    
    public static String getFancy() {
        return I18n.format("options.graphics.fancy", new Object[0]);
    }
    
    public static String getDefault() {
        return I18n.format("generator.default", new Object[0]);
    }
    
    static {
        splitter = Splitter.on('=').limit(2);
        pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    }
}
