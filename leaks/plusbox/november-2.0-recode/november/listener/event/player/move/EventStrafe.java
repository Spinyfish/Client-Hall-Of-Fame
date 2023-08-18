/* November.lol © 2023 */
package lol.november.listener.event.player.move;

import lol.november.listener.bus.Cancelable;
import net.minecraft.entity.Entity;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class EventStrafe extends Cancelable {

  private final Entity entity;
  private float rotationYaw, forward, strafe, friction;

  public EventStrafe(
    Entity entity,
    float rotationYaw,
    float forward,
    float strafe,
    float friction
  ) {
    this.entity = entity;
    this.rotationYaw = rotationYaw;
    this.forward = forward;
    this.strafe = strafe;
    this.friction = friction;
  }

  public Entity getEntity() {
    return entity;
  }

  public float getRotationYaw() {
    return rotationYaw;
  }

  public void setRotationYaw(float rotationYaw) {
    this.rotationYaw = rotationYaw;
  }

  public float getForward() {
    return forward;
  }

  public void setForward(float forward) {
    this.forward = forward;
  }

  public float getStrafe() {
    return strafe;
  }

  public void setStrafe(float strafe) {
    this.strafe = strafe;
  }

  public float getFriction() {
    return friction;
  }

  public void setFriction(float friction) {
    this.friction = friction;
  }
}
