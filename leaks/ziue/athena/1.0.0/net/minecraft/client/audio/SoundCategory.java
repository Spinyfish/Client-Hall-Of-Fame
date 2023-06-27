package net.minecraft.client.audio;

import java.util.*;
import com.google.common.collect.*;

public enum SoundCategory
{
    MASTER("master", 0), 
    MUSIC("music", 1), 
    RECORDS("record", 2), 
    WEATHER("weather", 3), 
    BLOCKS("block", 4), 
    MOBS("hostile", 5), 
    ANIMALS("neutral", 6), 
    PLAYERS("player", 7), 
    AMBIENT("ambient", 8);
    
    private static final Map<String, SoundCategory> NAME_CATEGORY_MAP;
    private static final Map<Integer, SoundCategory> ID_CATEGORY_MAP;
    private final String categoryName;
    private final int categoryId;
    
    private SoundCategory(final String name, final int id) {
        this.categoryName = name;
        this.categoryId = id;
    }
    
    public String getCategoryName() {
        return this.categoryName;
    }
    
    public int getCategoryId() {
        return this.categoryId;
    }
    
    public static SoundCategory getCategory(final String name) {
        return SoundCategory.NAME_CATEGORY_MAP.get(name);
    }
    
    static {
        NAME_CATEGORY_MAP = Maps.newHashMap();
        ID_CATEGORY_MAP = Maps.newHashMap();
        for (final SoundCategory soundcategory : values()) {
            if (SoundCategory.NAME_CATEGORY_MAP.containsKey(soundcategory.getCategoryName()) || SoundCategory.ID_CATEGORY_MAP.containsKey(soundcategory.getCategoryId())) {
                throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + soundcategory);
            }
            SoundCategory.NAME_CATEGORY_MAP.put(soundcategory.getCategoryName(), soundcategory);
            SoundCategory.ID_CATEGORY_MAP.put(soundcategory.getCategoryId(), soundcategory);
        }
    }
}
