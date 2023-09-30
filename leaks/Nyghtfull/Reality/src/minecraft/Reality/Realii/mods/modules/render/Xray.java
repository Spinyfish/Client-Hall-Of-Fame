package Reality.Realii.mods.modules.render;


import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class Xray
        extends Module {
    public static List<BlockPos> blocks = new ArrayList<>();
    public static Numbers<Number> searchtime = new Numbers<Number>("SearchTime", 100, 1, 1000, 1);
    public static Numbers<Number> range = new Numbers<Number>("Range", 60, 1, 150, 1);
    public static Option<Boolean> cave = new Option<>("Cave", true);
    public static Option<Boolean> coal = new Option<>("Coal", true);
    public static Option<Boolean> iron = new Option<>("Iron", true);
    public static Option<Boolean> gold = new Option<>("Gold", true);
    public static Option<Boolean> diammond = new Option<>("Diammond", true);
    SearchThread st;
    ArrayList<BlockPos> blocks1 = new ArrayList<>();
    ArrayList<BlockPos> blocks2 = new ArrayList<>();


    public Xray() {
        super("Xray", ModuleType.Render);
        addValues(searchtime, range, cave, coal, iron, gold, diammond);
    }


    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.ticksExisted % Xray.searchtime.getValue().intValue() == 0 && st == null) {
            st = new SearchThread();
            st.start();
        }
//        if (mc.playerController.curBlockDamageMP < 0.5) {
//            blocks1.clear();
//            for (int y = 10; y >= -10; --y) {
//                for (int x = -10; x <= 10; ++x) {
//                    for (int z = -10; z <= 10; ++z) {
//                        BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
//                        blocks1.add(pos);
//                    }
//                }
//            }
//        }
//        if (mc.playerController.curBlockDamageMP > 0.5) {
//            blocks2.clear();
//            for (int y = 10; y >= -10; --y) {
//                for (int x = -10; x <= 10; ++x) {
//                    for (int z = -10; z <= 10; ++z) {
//                        BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
//                        blocks2.add(pos);
//                    }
//                }
//            }
//
//            for (BlockPos b : blocks1) {
//                if (mc.theWorld.getBlockState(b).getBlock() != mc.theWorld.getBlockState(blocks2.get(blocks2.indexOf(b))).getBlock()) {
//                    blocks.add(b);
//                    System.out.println("Found real ores " + mc.theWorld.getBlockState(b).getBlock().getLocalizedName());
//                }
//
//            }
//
//        }

    }


    @EventHandler
    public void onRender(EventRender3D e) {
        setSuffix(SearchThread.blocks);
        for (BlockPos bp : blocks) {
            final BlockPos pos = bp;
            final Block block = mc.theWorld.getBlockState(pos).getBlock();
            final String s = block.getLocalizedName();
            final double n = pos.getX();
            final double x = n - RenderManager.renderPosX;
            final double n2 = pos.getY();
            final double y = n2 - RenderManager.renderPosY;
            final double n3 = pos.getZ();
            final double z = n3 - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(1, 1, 1, 0.15f);
            final double minX = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinX();
            final double minY = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinY();
            final double minZ = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinZ();
            if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.coal_block && (Boolean) Xray.coal.getValue()) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
                GL11.glColor4f(0.1f, 0.1f, 0.1f, 1.0f);
            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.iron_ore && (Boolean) Xray.iron.getValue()) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
                GL11.glColor4f(1, 0, 1, 1.0f);
            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.gold_ore && (Boolean) Xray.gold.getValue()) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
                GL11.glColor4f(1, 1, 0, 1.0f);
            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.diamond_ore && (Boolean) Xray.diammond.getValue()) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
                GL11.glColor4f(0, 1, 1, 1.0f);
            } else {
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                continue;
            }
            GL11.glLineWidth(1f);
            RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }


    @Override
    public void onEnable() {
        if (mc.theWorld == null) {
            return;
        }
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        blocks.clear();

    }

    public List<BlockPos> getBlocks() {
        return blocks;
    }
}

class SearchThread extends Thread {
    Minecraft mc = Minecraft.getMinecraft();
    public static int process = 0;
    public static int blocks = 0;

    @Override
    public void run() {
        super.run();
        blocks = 0;
        int reach = Xray.range.getValue().intValue();
        for (int y = reach; y >= -reach; --y) {
            for (int x = -reach; x <= reach; ++x) {
                for (int z = -reach; z <= reach; ++z) {
                    blocks++;
                    BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                    if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockOre) {
                        if (!Xray.blocks.contains(pos)) {
                            int i = 0;
                            if (mc.theWorld.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            } else if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)).getBlock() instanceof BlockAir) {
                                if (!Xray.blocks.contains(pos)) {
                                    i++;
                                }
                            }
                            if (i >= 1) {
                                if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.diamond_ore) {
                                    Helper.sendMessage("Find Diamond Ore!" + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                                }
                                if ((mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.diamond_ore && (Boolean) Xray.diammond.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.coal_ore && (Boolean) Xray.coal.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.iron_ore && (Boolean) Xray.iron.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.gold_ore && (Boolean) Xray.gold.getValue())) {
                                    Xray.blocks.add(pos);
                                }
                            } else if (!(Boolean) Xray.cave.getValue()) {
                                if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.diamond_ore) {
                                    Helper.sendMessage("Find Diamond Ore!" + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                                }
                                if ((mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.diamond_ore && (Boolean) Xray.diammond.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.coal_ore && (Boolean) Xray.coal.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.iron_ore && (Boolean) Xray.iron.getValue()) || (mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock() == Blocks.gold_ore && (Boolean) Xray.gold.getValue())) {
                                    Xray.blocks.add(pos);
                                }
                            }

                        }
                    }
                }
            }
        }
        ((Xray) ModuleManager.getModuleByClass(Xray.class)).st = null;
//        Xray.blocks.clear();

    }
}
