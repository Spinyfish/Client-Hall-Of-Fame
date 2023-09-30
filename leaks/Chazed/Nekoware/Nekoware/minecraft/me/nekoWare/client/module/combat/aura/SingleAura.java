
package me.nekoWare.client.module.combat.aura;

import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.combat.Aura;
import me.nekoWare.client.util.combat.CombatUtil;
import me.nekoWare.client.util.combat.RotationUtils;
import me.nekoWare.client.util.misc.Timer;
import me.nekoWare.client.util.packet.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SingleAura {
    private static Timer timer = new Timer();
    private static boolean block;
    public static boolean unblock;
    private static boolean unblocked;
    public static void doUpdate(final Aura aura, final UpdateEvent e, final Minecraft mc) {
        boolean invisible = aura.getBool("Invisibles");
        double range = aura.getDouble("Range");
        boolean players = aura.getBool("Players");
        boolean monsters = aura.getBool("Monsters");
        boolean teams = false;
        final Entity entity = CombatUtil.getTarget(teams, monsters, players, range, invisible);
        final boolean autoBlock = aura.getBool("Auto Block");
        final long clickSpeed = aura.getDouble("Click Speed").longValue();
        Aura.isBlocking = mc.thePlayer.getHeldItem() != null
                && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock && CombatUtil.canBlock(
                teams, false, true, range + 4.0, true);
        BlockPos a = new BlockPos(-0.2223, -0.132, -0.312);
        if (Aura.isBlocking) {
            if (e.isPost() && block) {
                //PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(a, 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
                unblock = true;
            }
        }
        block = true;
        Aura.currentEntity = (EntityLivingBase) entity;
        if (entity != null) {
        	if(Aura.Rots == true) {
			float[] rotations = RotationUtils.getRotations((EntityLivingBase) entity);
			e.setYaw(rotations[0]);
			e.setPitch(rotations[1]);
			mc.thePlayer.rotationYawHead = rotations[0];
			mc.thePlayer.rotationPitchHead = rotations[1];
			mc.thePlayer.renderYawOffset = rotations[0];
        	}
            if (unblocked) {
                mc.thePlayer.swingItem();
                PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                unblocked = false;
            }
            if (timer.delay(1000L / clickSpeed) && e.isPre()) {
                //PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, a.offsetDown(), EnumFacing.UP));
                unblocked = true;
                block = false;
                timer.reset();
            }
        }
    }
}
