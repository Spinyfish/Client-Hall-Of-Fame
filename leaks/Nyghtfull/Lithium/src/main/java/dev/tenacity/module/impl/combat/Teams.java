package dev.tenacity.module.impl.combat;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class Teams extends Module {

    public BooleanSetting scoreboard = new BooleanSetting("Scoreboard", false);
    public BooleanSetting armor = new BooleanSetting("Armor", false);
    public BooleanSetting color = new BooleanSetting("Color", false);

    public Teams() {
        super("Teams", Category.COMBAT, "Prevents Combat Modules from Attacking your Teammates.");
        this.addSettings(scoreboard, armor, color);
    }

    public boolean isTeammate(EntityLivingBase entity) {
        if (scoreboard.isEnabled() && mc.thePlayer.getTeam() != null && entity.getTeam() != null && mc.thePlayer.isOnSameTeam(entity)) {
            return true;
        }

        if (armor.isEnabled() && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            if (mc.thePlayer.inventory.armorInventory[3] != null && player.inventory.armorInventory[3] != null) {
                ItemStack myHead = mc.thePlayer.inventory.armorInventory[3];
                ItemArmor myItemArmor = (ItemArmor) myHead.getItem();

                ItemStack entityHead = player.inventory.armorInventory[3];
                ItemArmor entityItemArmor = (ItemArmor) entityHead.getItem();

                if (myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead)) {
                    return true;
                }
            }
        }

        if (color.isEnabled() && mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");

            return targetName.startsWith(String.format("§%s", clientName));
        }

        return false;
    }

}
