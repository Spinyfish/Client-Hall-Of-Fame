package tech.dort.dortware.api.module.enums;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum ModuleCategory {
    COMBAT, MOVEMENT, PLAYER, MISC, RENDER, EXPLOIT, HIDDEN;

    public String getName() {
        return StringUtils.capitalize(StringUtils.lowerCase(name()));
    }

    public static List<ModuleCategory> getAllReversed() {
        return Lists.reverse(Arrays.asList(ModuleCategory.values()));
    }
}
