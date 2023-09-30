package de.verschwiegener.atero.module.modules.combat;

import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class Antibots extends Module {
    TimeUtils timeUtils;

    public Antibots() {
        super("Antibots", "Antibots", Keyboard.KEY_NONE, Category.Combat);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Mineplex");
            for (final Object entity : mc.theWorld.getLoadedEntityList())
                if (entity instanceof EntityPlayer)
                    if ((isBot((EntityPlayer) entity) || ((EntityPlayer) entity).isInvisible()) && entity != mc.thePlayer) {
                        Killaura.bots.add((EntityPlayer) entity);
                        mc.theWorld.removeEntity((Entity) entity);
                    }

        }
    }

    boolean isBot(EntityPlayer player) {
        if (!isInTablist(player)) return true;
        return invalidName(player);
    }

    boolean isInTablist(EntityPlayer player) {
        if (Minecraft.getMinecraft().isSingleplayer())
            return false;
        for (final NetworkPlayerInfo playerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(player.getName()))
                return true;
        return false;
    }

    boolean invalidName(Entity e) {
        if (e.getName().contains("-"))
            return true;
        if (e.getName().contains("/"))
            return true;
        if (e.getName().contains("|"))
            return true;
        if (e.getName().contains("<"))
            return true;
        if (e.getName().contains(">"))
            return true;
        if (e.getName().contains("ยง"))
            return true;
        return false;
    }
}
