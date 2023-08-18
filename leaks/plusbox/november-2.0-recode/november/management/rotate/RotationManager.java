/* November.lol © 2023 */
package lol.november.management.rotate;

import lol.november.November;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.Stage;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.move.EventStrafe;
import lol.november.listener.event.player.move.EventWalkingUpdate;
import lol.november.management.rotate.correction.MoveCorrection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class RotationManager {

  /**
   * The {@link Minecraft} game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  private float[] client, server;
  private int rotationPriority;
  private int ticks;

  private MoveCorrection moveCorrection = MoveCorrection.NONE;

  public RotationManager() {
    server = new float[2];
    November.bus().subscribe(this);
  }

  @Subscribe
  private final Listener<EventWalkingUpdate> walkingUpdate = event -> {
    if (event.getStage() == Stage.PRE && client != null) {
      event.setYaw(client[0]);
      event.setPitch(client[1]);

      mc.thePlayer.rotationYawHead = client[0];
      mc.thePlayer.renderYawOffset = client[0];
      mc.thePlayer.renderPitch = client[1];

      if (--ticks <= 0) {
        moveCorrection = null;
        client = null;
      }
    }
  };

  @Subscribe
  private final Listener<EventStrafe> strafe = event -> {
    if (
      !event.getEntity().equals(mc.thePlayer) ||
      client == null ||
      moveCorrection == MoveCorrection.NONE
    ) return;

    if (moveCorrection == MoveCorrection.BASIC) {
      event.setRotationYaw(client[0]);
    } else if (moveCorrection == MoveCorrection.FREE) {
      // TODO

      event.setRotationYaw(client[0]);
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C03PacketPlayer packet) {
      if (!packet.getRotating()) return;

      server[0] = packet.getYaw();
      server[1] = packet.getPitch();
    }
  };

  public void spoof(int priority, int keepTicks, float[] angles) {
    if (client == null || priority >= rotationPriority) {
      client = angles;
      rotationPriority = priority;
      ticks = keepTicks;
    }
  }

  public void setMoveCorrection(int priority, MoveCorrection moveCorrection) {
    if (priority == rotationPriority) {
      this.moveCorrection = moveCorrection;
    }
  }

  public void reset(int priority) {
    if (priority >= rotationPriority || ticks <= 0) {
      ticks = 0;
      moveCorrection = MoveCorrection.NONE;
    }
  }

  public float[] server() {
    return server;
  }

  public float[] client() {
    return client;
  }

  public boolean spoofing() {
    return client != null && ticks > 0;
  }
}
