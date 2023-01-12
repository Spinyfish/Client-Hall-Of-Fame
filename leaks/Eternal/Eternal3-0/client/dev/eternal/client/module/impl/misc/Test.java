package dev.eternal.client.module.impl.misc;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.*;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Test", description = "Module used by developers to test out new features.", category = Module.Category.MISC, defaultKey = Keyboard.KEY_APOSTROPHE)
public class Test extends Module {

  private double speed, prevSpeed, x, y, z;
  private int count;
  private boolean damaged = false;

  public Test() {
    if (isEnabled()) toggle();
  }

  @Override
  protected void onDisable() {
  }

  @Override
  protected void onEnable() {

  }

  @Subscribe
  public void handleUpdate(EventUpdate update) {
  }

  @Subscribe
  public void handleMove(EventMove move) {
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove absoluteMove) {

  }

  @Subscribe
  public void handleAttack(EventAttack attack) {

  }

  @Subscribe
  public void handleBlockingAnimation(EventBlockingAnimation blockingAnimation) {

  }

  @Subscribe
  public void handleCollision(EventCollision collision) {

  }

  @Subscribe
  public void handleForceSprint(EventForceSprint forceSprint) {

  }

  @Subscribe
  public void handleJump(EventJump jump) {

  }

  @Subscribe
  public void handleKeyTyped(EventKeyTyped keyTyped) {

  }

  @Subscribe
  public void handlePacket(EventPacket packet) {
    if (packet.direction() == EventPacket.Direction.IN) {
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Subscribe
  public void handleRender2D(EventPostRenderGui render) {

  }

  @Subscribe
  public void handleRender3D(EventRender3D er) {
  }

  @Subscribe
  public void handleSlowdown(EventSlowdown slowdown) {

  }

  @Subscribe
  public void handleStep(EventStep step) {

  }

  @Subscribe
  public void handleTeleport(EventTeleport teleport) {
  }

  @Subscribe
  public void handleTick(EventTick tick) {

  }

  @Subscribe
  public void handleTransactionPing(EventTransactionPing transactionPing) {

  }
}
