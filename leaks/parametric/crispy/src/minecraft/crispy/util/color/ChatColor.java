package crispy.util.color;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatColor {
    public String translateAlternateColorCodes(String translate) {
        return translate.replaceAll("&", "\247");
    }
}
