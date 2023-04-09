package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.networking.ServerUtils;

import static org.lwjgl.opengl.GL11.*;

public class Breaker extends Module {

    private static final EnumFacing[] DIRECTIONS = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};

    private final NumberValue distance = new NumberValue("Range", this, 4, 1, 6, true);
    private final BooleanValue surrounding = new BooleanValue("Break Surrounding", this, true);
    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values());
    private final BooleanValue render = new BooleanValue("Render", this, true);
    public final NumberValue width = new NumberValue("Width", this, 3, 1, 5, true, render);
    private BlockPos toAttack;

    public Breaker(ModuleData moduleData) {
        super(moduleData);
        register(mode, distance, width, surrounding, render);
    }

    @Subscribe
    public void onPacket(PacketEvent event){
        if(event.getPacket() instanceof S02PacketChat){
            final S02PacketChat chat = event.getPacket();
            final String text = chat.getChatComponent().getUnformattedText();
            event.forceCancel(text.equals("Game> Cakes cannot be eaten for the first 20 seconds.") || text.equals("Game> You cannot eat your own cake!"));
        }
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (render.getValue()) {
            drawCircle(mc.thePlayer, mc.timer.renderPartialTicks);
        }
    }

    @Subscribe
    public void handlePlayerUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case NORMAL: {
                if (event.isPre()) {
                    final double distance = this.distance.getValue() + 2;
                    final boolean breakSurrounding = surrounding.getValue();
                    final int distanceValue = (int) distance;
                    for (int x = -distanceValue; x < distanceValue; ++x) {
                        for (int y = distanceValue; y > -distanceValue; --y) {
                            for (int z = -distanceValue; z < distanceValue; ++z) {
                                final double xPos = mc.thePlayer.posX + x;
                                final double yPos = mc.thePlayer.posY + y;
                                final double zPos = mc.thePlayer.posZ + z;
                                BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                                Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                                if (block != Blocks.cake && block != Blocks.bed) {
                                    continue;
                                }
                                if (!isOpen(blockPos) && breakSurrounding) {
                                    for (EnumFacing direction : DIRECTIONS) {
                                        BlockPos toCheck = blockPos.offset(direction);
                                        if (!(mc.theWorld.getBlockState(toCheck).getBlock() instanceof BlockAir)) {
                                            BlockPos preventingBlock = blockPos.offset(direction);
                                            breakBlock(preventingBlock);
                                            return;
                                        }
                                    }
                                }
                                breakBlock(blockPos);
                            }
                        }
                    }
                }
                break;
            }
            case ANNIHILATION: {
                if (mc.objectMouseOver != null &&
                        mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                        && mc.playerController.curBlockDamageMP >=
                        mc.theWorld.getBlockState(mc.objectMouseOver.func_178782_a()).getBlock().getBlockHardness() - 0.05F) {
                    this.toAttack = mc.objectMouseOver.func_178782_a();
                }
                if (toAttack != null) {
                    if (mc.thePlayer.getDistanceSq(toAttack) >= 48.0F) {
                        toAttack = null;
                        break;
                    }
                    breakBlock(toAttack);
                }
                break;
            }
        }
    }

    private void drawCircle(Entity entity, float partialTicks) {
        glPushMatrix();
        glColor3d(255, 255, 255);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(width.getValue().floatValue());
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;


        final double pix2 = Math.PI * 2.0D;
        for (int i = 0; i <= 90; ++i) {
            glVertex3d(x + (distance.getValue() - 0.5) * Math.cos(i * pix2 / 45), y, z + (distance.getValue() - 0.5) * Math.sin(i * pix2 / 45));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    private void breakBlock(BlockPos blockPos) {
        if (ServerUtils.onHypixel()) {
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
        }
        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
    }

    private boolean isOpen(BlockPos blockPos) {
        for (EnumFacing direction : DIRECTIONS) {
            BlockPos toCheck = blockPos.offset(direction);
            if (mc.theWorld.getBlockState(toCheck).getBlock() instanceof BlockAir)
                return true;
        }
        return false;
    }

    @Override
    public String getSuffix() {
        int dist = distance.getCastedValue().intValue();
        return " \2477" + dist;
    }

    public enum Mode implements INameable {
        NORMAL("Normal"), ANNIHILATION("Annihilation");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
