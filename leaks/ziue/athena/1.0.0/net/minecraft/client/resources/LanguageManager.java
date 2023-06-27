package net.minecraft.client.resources;

import net.minecraft.client.resources.data.*;
import java.io.*;
import net.minecraft.util.*;
import java.util.*;
import com.google.common.collect.*;
import org.apache.logging.log4j.*;

public class LanguageManager implements IResourceManagerReloadListener
{
    private static final Logger logger;
    private final IMetadataSerializer theMetadataSerializer;
    private String currentLanguage;
    protected static final Locale currentLocale;
    private Map<String, Language> languageMap;
    
    public LanguageManager(final IMetadataSerializer theMetadataSerializerIn, final String currentLanguageIn) {
        this.languageMap = (Map<String, Language>)Maps.newHashMap();
        this.theMetadataSerializer = theMetadataSerializerIn;
        this.currentLanguage = currentLanguageIn;
        I18n.setLocale(LanguageManager.currentLocale);
    }
    
    public void parseLanguageMetadata(final List<IResourcePack> p_135043_1_) {
        this.languageMap.clear();
        for (final IResourcePack iresourcepack : p_135043_1_) {
            try {
                final LanguageMetadataSection languagemetadatasection = iresourcepack.getPackMetadata(this.theMetadataSerializer, "language");
                if (languagemetadatasection == null) {
                    continue;
                }
                for (final Language language : languagemetadatasection.getLanguages()) {
                    if (!this.languageMap.containsKey(language.getLanguageCode())) {
                        this.languageMap.put(language.getLanguageCode(), language);
                    }
                }
            }
            catch (RuntimeException runtimeexception) {
                LanguageManager.logger.warn("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName(), (Throwable)runtimeexception);
            }
            catch (IOException ioexception) {
                LanguageManager.logger.warn("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName(), (Throwable)ioexception);
            }
        }
    }
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        final List<String> list = (List<String>)Lists.newArrayList((Object[])new String[] { "en_US" });
        if (!"en_US".equals(this.currentLanguage)) {
            list.add(this.currentLanguage);
        }
        LanguageManager.currentLocale.loadLocaleDataFiles(resourceManager, list);
        StringTranslate.replaceWith(LanguageManager.currentLocale.properties);
    }
    
    public boolean isCurrentLocaleUnicode() {
        return LanguageManager.currentLocale.isUnicode();
    }
    
    public boolean isCurrentLanguageBidirectional() {
        return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
    }
    
    public void setCurrentLanguage(final Language currentLanguageIn) {
        this.currentLanguage = currentLanguageIn.getLanguageCode();
    }
    
    public Language getCurrentLanguage() {
        return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("en_US");
    }
    
    public SortedSet<Language> getLanguages() {
        return (SortedSet<Language>)Sets.newTreeSet((Iterable)this.languageMap.values());
    }
    
    static {
        logger = LogManager.getLogger();
        currentLocale = new Locale();
    }
}
