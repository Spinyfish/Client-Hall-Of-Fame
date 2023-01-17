package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.BlockBreakEvent;
import com.alan.clients.newevent.impl.other.BlockDamageEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.BoundsNumberValue;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.SlotUtil;
import util.time.StopWatch;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Auth (the retard; horrible code)
 * @since 24/06/2022
 */

@Rise
@ModuleInfo(name = "Auto Tool", description = "module.player.autotool.description", category = Category.PLAYER)
public class AutoTool extends Module {

    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 100, 150, 0, 500, 25);
    private final BooleanValue sword = new BooleanValue("Sword", this, true);
    private final BooleanValue silent = new BooleanValue("Silent", this, true);

    private final StopWatch stopWatch = new StopWatch();
    private long currentDelay;
    private boolean reset;
    private int serverSideSlot = -1;
    private int tool = -1;

    public void onDisable() {
        if (serverSideSlot != mc.thePlayer.inventory.currentItem) {
            PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        final Packet<?> p = event.getPacket();

        if (p instanceof C08PacketPlayerBlockPlacement) {
            serverSideSlot = mc.thePlayer.inventory.currentItem;
        }

        if (!silent.getValue()) {
            return;
        }

        if (p instanceof C07PacketPlayerDigging) {
            final C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) p;
            final int slot = this.findTool(wrapper.getPosition());

            if (slot != -1) {
                this.tool = slot;
                serverSideSlot = tool;
            }
        }

        if (sword.getValue() && p instanceof C02PacketUseEntity) {
            final int slot = SlotUtil.findSword();

            if (slot != -1) {
                this.tool = slot;
                serverSideSlot = tool;
            }
        }

        if (p instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange wrapper = (C09PacketHeldItemChange) p;
            serverSideSlot = wrapper.getSlotId();
            stopWatch.reset();
        }
    };

    @EventLink()
    public final Listener<BlockDamageEvent> onBlockDamage = event -> {

        if (silent.getValue()) {
            event.setRelativeBlockHardness(getPlayerRelativeBlockHardness(event.getPlayer(), event.getWorld(), event.getBlockPos(), serverSideSlot));
        }
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.gameSettings.keyBindAttack.isKeyDown() && serverSideSlot != mc.thePlayer.inventory.currentItem) {
            SlotComponent.setSlot(serverSideSlot, true);
        }

        if (!mc.gameSettings.keyBindAttack.isKeyDown() && stopWatch.finished(60)) {
            if (serverSideSlot != mc.thePlayer.inventory.currentItem) {
                serverSideSlot = mc.thePlayer.inventory.currentItem;
                reset = false;
                tool = -1;
                stopWatch.reset();
                return;
            }
        }

        if (silent.getValue()) {
            return;
        }

        if (tool != -1) {
            if (!reset) {
                this.currentDelay = Math.round(MathUtil.getRandom(this.delay.getValue().longValue(), this.delay.getSecondValue().longValue()));
                stopWatch.reset();
                reset = true;
            }

            if (stopWatch.finished(this.currentDelay)) {
                if (silent.getValue()) {
                    if (serverSideSlot != tool) {
                        PacketUtil.send(new C09PacketHeldItemChange(tool));
                        serverSideSlot = tool;
                    }

                } else {
                    if (mc.thePlayer.inventory.currentItem != tool) {
                        mc.thePlayer.inventory.currentItem = tool;
                    }
                }

                reset = false;
                tool = -1;
            }
        }
    };

    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {

        if (!sword.getValue()) {
            return;
        }

        final int slot = SlotUtil.findSword();

        if (slot != -1) {
            this.tool = slot;
        }
    };

    @EventLink()
    public final Listener<BlockBreakEvent> onBlockBreak = event -> {

        final int slot = this.findTool(event.getBlockPos());

        if (slot != -1) {
            this.tool = slot;
        }
    };

    private int findTool(final BlockPos blockPos) {
        float bestSpeed = 1;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            final float speed = itemStack.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    public ItemStack getCurrentItemInSlot(final int slot) {
        return slot < 9 && slot >= 0 ? mc.thePlayer.inventory.mainInventory[slot] : null;
    }

    public float getStrVsBlock(final Block blockIn, final int slot) {
        float f = 1.0F;

        if (mc.thePlayer.inventory.mainInventory[slot] != null) {
            f *= mc.thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public float getPlayerRelativeBlockHardness(final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final int slot) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        final float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0F ? 0.0F : (!canHeldItemHarvest(block, slot) ? getToolDigEfficiency(block, slot) / f / 100.0F : getToolDigEfficiency(block, slot) / f / 30.0F);
    }

    public boolean canHeldItemHarvest(final Block blockIn, final int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            final ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(slot);
            return itemstack != null && itemstack.canHarvestBlock(blockIn);
        }
    }

    public float getToolDigEfficiency(final Block blockIn, final int slot) {
        float f = getStrVsBlock(blockIn, slot);

        if (f > 1.0F) {
            final int i = EnchantmentHelper.getEfficiencyModifier(mc.thePlayer);
            final ItemStack itemstack = getCurrentItemInSlot(slot);

            if (i > 0 && itemstack != null) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            final float f1;

            switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;

                case 1:
                    f1 = 0.09F;
                    break;

                case 2:
                    f1 = 0.0027F;
                    break;

                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
            f /= 5.0F;
        }

        if (!mc.thePlayer.onGround) {
            f /= 5.0F;
        }

        return f;
    }
}