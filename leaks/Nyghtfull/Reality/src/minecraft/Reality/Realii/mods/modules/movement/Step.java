/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Step
extends Module {
    private Numbers<Number> height = new Numbers<Number>("Height", "height", 1.0, 1.0, 10.0, 0.5);
    private Option<Boolean> ncp = new Option<Boolean>("NCP", "ncp", false);
    private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);

    public Step() {
        super("Step", ModuleType.Movement);
        this.addValues(this.ncp, height);
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.stepHeight = 0.6f;
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (this.ncp.getValue().booleanValue()) {
            this.mc.thePlayer.stepHeight = 0.6f;
            if (this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.isCollided && this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.isCollided) {
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.753, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ);
                this.mc.timer.timerSpeed = 0.5f;
                this.mc.thePlayer.setSprinting(true);
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100L);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        Step.this.mc.timer.timerSpeed = 1.0f;
                    }
                }).start();
            }
        } else {
            this.mc.thePlayer.stepHeight = 1.0f;
        }
    }

}

