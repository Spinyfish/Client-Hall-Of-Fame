/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script.wrapper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ScriptWorld {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float getTimer() {
        return ScriptWorld.mc.getTimer().timerSpeed;
    }

    public static void setTimer(float timerSpeed) {
        ScriptWorld.mc.getTimer().timerSpeed = timerSpeed;
    }

    public static List<EntityLivingBase> getEntityLivingBases() {
        ArrayList<EntityLivingBase> toReturn = new ArrayList<EntityLivingBase>();
        for (Entity e : ScriptWorld.mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityLivingBase)) continue;
            toReturn.add((EntityLivingBase)e);
        }
        return toReturn;
    }

    public static List<EntityPlayer> getLoadedPlayers() {
        return ScriptWorld.mc.theWorld.playerEntities;
    }
}

