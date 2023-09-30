package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @author cedo
 * @since 03/24/2022
 */
public class XRay extends Module {
    public static final NumberSetting opacity = new NumberSetting("Opacity", 60, 100, 0, 1);
    public static final BooleanSetting bypass = new BooleanSetting("Anti-xray bypass", false);
    public static final HashSet<BlockPosition> checkedOres = new HashSet<>(), queuedOres = new HashSet<>(), oresToRender = new HashSet<>();
    public static boolean enabled;
    private static BooleanSetting redstone, diamond, emerald, lapis, iron, coal, gold;
    private final MultipleBoolSetting ores = new MultipleBoolSetting("Ores",
            redstone = new BooleanSetting("Redstone", true),
            diamond = new BooleanSetting("Diamond", true),
            emerald = new BooleanSetting("Emerald", true),
            lapis = new BooleanSetting("Lapis", true),
            iron = new BooleanSetting("Iron", true),
            coal = new BooleanSetting("Coal", true),
            gold = new BooleanSetting("Gold", true)
    );
    private final HashSet<Block> blocks = new HashSet<>(Arrays.asList(Blocks.obsidian, Blocks.clay, Blocks.mossy_cobblestone, Blocks.diamond_ore, Blocks.redstone_ore, Blocks.iron_ore, Blocks.coal_ore, Blocks.gold_ore, Blocks.emerald_ore, Blocks.lapis_ore));
    private final TimerUtil updateTimer = new TimerUtil(), searchTimer = new TimerUtil();
    private World lastWorld;


    public XRay() {
        super("XRay", Category.RENDER, "Shows blocks through the world");
        addSettings(ores, opacity, bypass);
    }

    @Override
    public void onStrafeEvent(StrafeEvent e) {
        if (bypass.isEnabled() && !mc.isSingleplayer()) {
            if (updateTimer.hasTimeElapsed(2000)) {
                search();
                updateTimer.reset();
            }
            if (!queuedOres.isEmpty()) {
                Multithreading.runAsync(() -> {
                    for (int i = 0; i < 18; i++) {
                        if (queuedOres.size() < 1 || queuedOres.size() < i + 1) return;
                        BlockPosition pos = queuedOres.iterator().next();
                        queuedOres.remove(pos);
                        PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        checkedOres.add(pos);
                        Multithreading.schedule(() -> checkedOres.remove(pos), 30, TimeUnit.SECONDS);
                    }
                });
            }
        }
    }

    public static boolean isWhitelisted(Block block) {
        for (Ore o : Ore.values()) {
            if (o.ore == block) {
                return o.setting.isEnabled();
            }
        }
        return false;
    }

    public static boolean isExposed(IBlockAccess worldIn, BlockPosition pos, EnumFacing side, double minY, double maxY, double minZ, double maxZ, double minX, double maxX) {
        return side == EnumFacing.DOWN && minY > 0.0D || (side == EnumFacing.UP && maxY < 1.0D || (side == EnumFacing.NORTH && minZ > 0.0D || (side == EnumFacing.SOUTH && maxZ < 1.0D || (side == EnumFacing.WEST && minX > 0.0D || (side == EnumFacing.EAST && maxX < 1.0D || !worldIn.getBlockState(pos).getBlock().isOpaqueCube())))));
    }

    @Override
    public void onEnable() {
        enabled = true;
        mc.renderGlobal.loadRenderers();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        enabled = false;
        super.onDisable();
        mc.renderGlobal.loadRenderers();
    }

    public void search() {
        if (mc.theWorld != null && lastWorld != mc.theWorld) {
            lastWorld = mc.theWorld;
            oresToRender.clear();
            checkedOres.clear();
            queuedOres.clear();
        }

        int radius = 3;
        Multithreading.runAsync(() -> {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPosition pos = new BlockPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        Block block = mc.theWorld.getBlockState(pos).getBlock();

                        if (block instanceof BlockOre || block instanceof BlockRedstoneOre) {
                            if (!checkedOres.contains(pos) && !queuedOres.contains(pos) && blocks.contains(block)) {
                                if (shouldAdd(block, pos)) {
                                    queuedOres.add(pos);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean shouldAdd(Block block, BlockPosition pos) {
        for (EnumFacing si : EnumFacing.VALUES) {
            if (isExposed(mc.theWorld, pos.offset(si), si, block.getBlockBoundsMinY(), block.getBlockBoundsMaxY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxZ(), block.getBlockBoundsMinX(), block.getBlockBoundsMaxX())) {
                return false;
            }
        }
        return true;
    }

    private enum Ore {
        DIAMOND(Blocks.diamond_ore, new Color(93, 235, 245), diamond),
        IRON(Blocks.iron_ore, Color.WHITE, iron),
        GOLD(Blocks.gold_ore, new Color(239, 236, 79), gold),
        REDSTONE(Blocks.redstone_ore, Color.RED, redstone),
        REDSTONE_LIT(Blocks.lit_redstone_ore, Color.RED, redstone),
        COAL(Blocks.coal_ore, Color.BLACK, coal),
        LAPIS(Blocks.lapis_ore, new Color(21, 69, 185), lapis),
        EMERALD(Blocks.emerald_ore, new Color(23, 221, 98), emerald);

        private final Block ore;
        private final Color color;
        private final BooleanSetting setting;

        Ore(Block ore, Color color, BooleanSetting setting) {
            this.ore = ore;
            this.color = color;
            this.setting = setting;
        }
    }

}
