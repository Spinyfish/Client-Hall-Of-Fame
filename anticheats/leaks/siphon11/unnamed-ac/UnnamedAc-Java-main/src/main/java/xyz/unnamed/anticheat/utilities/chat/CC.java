package xyz.unnamed.anticheat.utilities.chat;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2024
 */
public class CC {

    public static final String API_FAILED =
            ChatColor.RED + "The API failed to retrieve your information. Try again later.";

    public static final String NO_PERMISSION =
            ChatColor.RED + "You do not have permission to do this.";


    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKE = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String BLANK_LINE = "§a §b §c §d §e §f §0 §r";
    public static final String SCOREBOARD_LINE = CC.GRAY + CC.STRIKE + "----------------------";
    public static final String LONG_CHAT_LINE = CC.GRAY + CC.STRIKE + StringUtils.repeat("-", 53);
    public static final String SHORT_CHAT_LINE = CC.GRAY + CC.STRIKE + StringUtils.repeat("-", 32);
    public static final String UNICODE_VERTICAL_BAR = CC.GRAY + StringEscapeUtils.unescapeJava("\u2503");

    public static final String UNICODE_HEART = CC.RED + StringEscapeUtils.unescapeJava("\u2764");

    public static final String UNICODE_ARROW_LEFT = CC.GRAY + StringEscapeUtils.unescapeJava("\u25C0");
    public static final String UNICODE_ARROW_RIGHT = CC.GRAY + StringEscapeUtils.unescapeJava("\u25B6");
    public static final String UNICODE_ARROWS_LEFT = CC.GRAY + StringEscapeUtils.unescapeJava("\u00AB");
    public static final String UNICODE_ARROWS_RIGHT = CC.GRAY + StringEscapeUtils.unescapeJava("\u00BB");

    public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
    public static final String DOT = StringEscapeUtils.unescapeJava("\u25CF");

    public static String strip(String in) {
        return ChatColor.stripColor(translate(in));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static String format(String in, Object... objects) { return translate(String.format(in, objects)); }

    public static List<String> translateLines(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }

}
