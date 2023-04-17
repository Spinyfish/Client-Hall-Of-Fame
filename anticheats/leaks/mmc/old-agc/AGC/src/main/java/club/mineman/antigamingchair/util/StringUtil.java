package club.mineman.antigamingchair.util;

import java.util.Arrays;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class StringUtil {
   public static final String NO_PERMISSION;
   public static final String IP_BAN;
   public static final String IP_BAN_OTHER;
   public static final String PERMANENT_BAN;
   public static final String TEMPORARY_BAN;
   public static final String KICK;
   public static final String PERMANENT_MUTE;
   public static final String TEMPORARY_MUTE;
   public static final String BLACKLIST;
   public static final String COMMAND_COOLDOWN;
   public static final String PLAYER_ONLY;
   public static final String CHAT_COOLDOWN;
   public static final String PLAYER_NOT_FOUND;
   public static final String LOAD_ERROR;
   public static final String SPLIT_PATTERN;

   private StringUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static int jsonToInt(Object obj) {
      return (Integer) (obj instanceof Long ? Integer.valueOf(((Long) obj).intValue()) : obj);
   }

   public static String buildMessage(String[] args, int start) {
      return start >= args.length?"":ChatColor.stripColor(String.join(" ", (CharSequence[])Arrays.copyOfRange(args, start, args.length)));
   }

   public static String getFirstSplit(String s) {
      return s.split(SPLIT_PATTERN)[0];
   }

   static {
      NO_PERMISSION = CC.RED + "You don\'t have permission to use this command.";
      IP_BAN = CC.RED + "You IP is permanently banned from Mineman Club.\n" + CC.GRAY + "If you feel this ban is unjustified, email appeal@mineman.club.\n" + CC.GOLD + "You may also purchase an unban at store.mineman.club.";
      IP_BAN_OTHER = CC.RED + "You IP is banned from Mineman Club because of a punishment related to %s.\n" + CC.GRAY + "If you feel this ban is unjustified, email appeal@mineman.club.\n" + CC.GOLD + "You may also purchase an unban at store.mineman.club.";
      PERMANENT_BAN = CC.RED + "You are permanently banned from Mineman Club.\n" + CC.GRAY + "If you feel this ban is unjustified, email appeal@mineman.club.\n" + CC.GOLD + "You may also purchase an unban at store.mineman.club.";
      TEMPORARY_BAN = CC.RED + "You are banned from Mineman Club for %s.\n" + CC.GRAY + "If you feel this ban is unjustified, email appeal@mineman.club.\n" + CC.GOLD + "You may also purchase an unban at store.mineman.club.";
      KICK = CC.RED + "You have been kicked from Mineman Club.";
      PERMANENT_MUTE = CC.RED + "You are permanently muted.";
      TEMPORARY_MUTE = CC.RED + "You are temporarily muted for %s.";
      BLACKLIST = CC.RED + "You are blacklisted from Mineman Club.\n" + CC.GRAY + "You may not appeal this type of ban.\n" + CC.DARK_RED + "You may also not purchase an unban for this type of ban.";
      COMMAND_COOLDOWN = CC.RED + "You can\'t use command that fast.";
      PLAYER_ONLY = CC.RED + "Only players can use this command.";
      CHAT_COOLDOWN = CC.RED + "You can\'t chat that fast.";
      PLAYER_NOT_FOUND = CC.RED + "%s not found.";
      LOAD_ERROR = CC.RED + "An error occurred while loading your player data. Try again later.";
      SPLIT_PATTERN = Pattern.compile("\\s").pattern();
   }
}
