package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class LanguageManager implements IResourceManagerReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private final IMetadataSerializer theMetadataSerializer;
    private String currentLanguage;
    protected static final Locale currentLocale = new Locale();
    private final Map<String, Language> languageMap = Maps.newHashMap();

    public LanguageManager(final IMetadataSerializer theMetadataSerializerIn, final String currentLanguageIn) {
        this.theMetadataSerializer = theMetadataSerializerIn;
        this.currentLanguage = currentLanguageIn;
        I18n.setLocale(currentLocale);
    }

    public void parseLanguageMetadata(final List<IResourcePack> p_135043_1_) {
        this.languageMap.clear();

        for (final IResourcePack iresourcepack : p_135043_1_) {
            try {
                final LanguageMetadataSection languagemetadatasection = iresourcepack.getPackMetadata(this.theMetadataSerializer, "language");

                if (languagemetadatasection != null) {
                    for (final Language language : languagemetadatasection.getLanguages()) {
                        if (!this.languageMap.containsKey(language.getLanguageCode())) {
                            this.languageMap.put(language.getLanguageCode(), language);
                        }
                    }
                }
            } catch (final RuntimeException runtimeexception) {
                logger.warn("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName(), runtimeexception);
            } catch (final IOException ioexception) {
                logger.warn("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName(), ioexception);
            }
        }
    }

    public void onResourceManagerReload(final IResourceManager resourceManager) {
        final List<String> list = Lists.newArrayList("en_US");

        if (!"en_US".equals(this.currentLanguage)) {
            list.add(this.currentLanguage);
        }

        currentLocale.loadLocaleDataFiles(resourceManager, list);
        StringTranslate.replaceWith(currentLocale.properties);
    }

    public boolean isCurrentLocaleUnicode() {
        return currentLocale.isUnicode();
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
        return Sets.newTreeSet(this.languageMap.values());
    }
}
