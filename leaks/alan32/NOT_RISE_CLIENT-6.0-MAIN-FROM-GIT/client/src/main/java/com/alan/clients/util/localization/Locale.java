package com.alan.clients.util.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

/**
 * @author Hazsi
 * @since 10/31/2022
 */
@Getter
@RequiredArgsConstructor
public enum Locale {
    ZH_ZH("zh_ZH"),
    EN_US("en_US"),
    FR_FR("fr_FR"),
    SV_SE("sv_SE"),
    PL_PL("pl_PL"),
    RU_RU("ru_RU");

    private final String file;
    private final HashMap<String, String> strings = new HashMap<>();
}