/**
 * @project Myth
 * @author CodeMan
 * @at 17.09.22, 14:40
 */
package dev.myth.api.utils;

import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class TeamUtil implements IMethods {

    public boolean isOnSameTeamName(EntityPlayer entityPlayer) {
        String name = entityPlayer.getDisplayName().getFormattedText();
        String ownName = MC.thePlayer.getDisplayName().getFormattedText();

        if (name.contains("§") && ownName.contains("§")) {
            if (name.length() > name.indexOf("§") + 1 && ownName.length() > ownName.indexOf("§") + 1) {
                return name.charAt(name.indexOf("§") + 1) == ownName.charAt(ownName.indexOf("§") + 1);
            }
        }
        return false;
    }

}
