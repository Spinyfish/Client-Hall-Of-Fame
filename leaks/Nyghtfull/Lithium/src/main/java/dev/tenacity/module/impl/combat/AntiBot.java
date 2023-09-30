package dev.tenacity.module.impl.combat;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {

    public BooleanSetting blocksmc = new BooleanSetting("BlocksMC", true);

    public AntiBot() {
        super("AntiBot", Category.COMBAT, "Prevents Combat Modules from attacking Bots.");

        this.addSettings(blocksmc);
    }

    public boolean isBot(EntityLivingBase entity) {
        if (blocksmc.isEnabled() && entity.getDisplayName().getFormattedText().contains("SHOP") || entity.getDisplayName().getFormattedText().contains("UPGRADES") || entity.getDisplayName().getFormattedText().contains("CIT-")) {
            return true;
        }

        return false;
    }
}