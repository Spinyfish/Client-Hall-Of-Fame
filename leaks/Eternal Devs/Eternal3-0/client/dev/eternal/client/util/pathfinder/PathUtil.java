package dev.eternal.client.util.pathfinder;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.player.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class PathUtil {

  private static PathUtil singleton;

  private final Minecraft mc = Minecraft.getMinecraft();
  private PathInfo pathInfo;
  private boolean isPathing;
  private float[] rotations = new float[2];
  private Node currentNode;

  private PathUtil() {
    Client.singleton().eventBus().register(this);
  }

  public void doPath(PathInfo pathInfo) {
    this.pathInfo = pathInfo;
    isPathing = true;
  }

  @Subscribe
  public void onMove(EventMove eventMove) {
    if (pathInfo.pathList().isEmpty()) {
      isPathing = false;
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    } else {
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }
    if (currentNode == null && !pathInfo.pathList().isEmpty())
      currentNode = pathInfo.pathList().get(0);
    if (currentNode == null || !isPathing) return;

    final BlockPos bp = currentNode.blockPos();
    rotations = RotationUtil.getRotations(mc.thePlayer.getPositionEyes(1.0F), new Vec3(
        bp.getX() + 0.5,
        bp.getY() + 0.5,
        bp.getZ() + 0.5
    ));
    eventMove.yaw(rotations[0]);
    MovementUtil.performMoveFix(eventMove);

    if (mc.thePlayer.getDistance(bp.getX() + 0.5, bp.getY(), bp.getZ() + 0.5) < 0.5) {
      pathInfo.pathList().remove(0);
      currentNode = null;
    }
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    if (isPathing) eventUpdate.rotation().rotationYaw(rotations[0]);
  }

  @Subscribe
  public void onRender3D(EventRender3D eventRender3D) {
    if (!isPathing) return;
    GlStateManager.pushMatrix();
    GlStateManager.translate(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
    GlStateManager.disableTexture2D();
    GlStateManager.color(1, 1, 1, 1);
    GL11.glBegin(3);
    pathInfo.pathList().forEach(node -> {
      final BlockPos bp = node.blockPos();
      GL11.glVertex3d(bp.getX() + 0.5, bp.getY() + 0.1, bp.getZ() + 0.5);
    });
    GL11.glEnd();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }

  public static PathUtil getSingleton() {
    if (singleton == null) ;
    singleton = new PathUtil();

    return singleton;
  }

}
