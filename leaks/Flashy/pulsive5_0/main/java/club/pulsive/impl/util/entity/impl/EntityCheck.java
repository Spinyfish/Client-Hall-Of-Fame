package club.pulsive.impl.util.entity.impl;

import club.pulsive.impl.module.impl.exploit.Disabler;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.util.entity.ICheck;
import club.pulsive.impl.util.player.DisablerUtility;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.player.RotationUtil;
import club.pulsive.impl.util.world.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

import java.util.function.Supplier;

public final class EntityCheck implements ICheck {
    private final boolean players, animals, monsters, invisibles;

    public EntityCheck(boolean players, boolean animals, boolean monsters, boolean invisibles) {
        this.players = players;
        this.animals = animals;
        this.monsters = monsters;
        this.invisibles = invisibles;
    }

    @Override
    public boolean validate(Entity entity) {
        final IChatComponent displayName = entity.getDisplayName();
        final String formattedText = displayName.getFormattedText();
        displayName.getUnformattedText();
        final boolean b = !formattedText.substring(0, formattedText.length() - 2).contains("§");
        final boolean contains = formattedText.substring(formattedText.length() - 2).contains("§");
        if (entity instanceof EntityPlayerSP) {
            return false;
        }


        if (!invisibles && entity.isInvisible()) {
            return false;
        }

        if (animals && entity instanceof EntityAnimal) {
            return true;
        }

        if(players && entity instanceof EntityPlayer)
            return true;

        if(b && contains)
            return false;

        return monsters && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
    }
}
