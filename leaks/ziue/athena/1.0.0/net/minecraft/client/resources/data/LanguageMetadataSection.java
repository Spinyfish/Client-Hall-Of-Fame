package net.minecraft.client.resources.data;

import java.util.*;
import net.minecraft.client.resources.*;

public class LanguageMetadataSection implements IMetadataSection
{
    private final Collection<Language> languages;
    
    public LanguageMetadataSection(final Collection<Language> p_i1311_1_) {
        this.languages = p_i1311_1_;
    }
    
    public Collection<Language> getLanguages() {
        return this.languages;
    }
}
