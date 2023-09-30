/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.MathHelper;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.FriendManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.player.Teams;

import java.util.ArrayList;

public class BowAimBot
extends Module {
    private Option<Boolean> lockView = new Option<Boolean>("Lockview", "lockview", false);
    public static ArrayList<Entity> attackList = new ArrayList();
    public static ArrayList<Entity> targets = new ArrayList();
    public static int currentTarget;

    public BowAimBot() {
        super("BowAimBot", "Bow Aim", ModuleType.Combat);
        this.addValues(this.lockView);
    }

    public boolean isValidTarget(Entity entity) {
        boolean valid = false;
        if (entity == this.mc.thePlayer.ridingEntity) {
            return false;
        }
        if (entity.isInvisible()) {
            valid = true;
        }
        if (FriendManager.isFriend(entity.getName()) && entity instanceof EntityPlayer || !this.mc.thePlayer.canEntityBeSeen(entity) || Teams.isOnSameTeam(entity) || !(entity instanceof EntityLivingBase) && entity.isEntityAlive()) {
            return false;
        }
        if (entity != mc.thePlayer) {
            valid = entity != null && this.mc.thePlayer.getDistanceToEntity(entity) <= 50.0f && entity != this.mc.thePlayer && entity.isEntityAlive() && !FriendManager.isFriend(entity.getName());
        }
        return valid;
    }

    @EventHandler
    public void onPre(EventPreUpdate pre) {
        Entity e;
        for (Object o : this.mc.theWorld.loadedEntityList) {
            e = (Entity)o;
            if (e instanceof EntityPlayer && !targets.contains(e)) {
                targets.add(e);
            }
            if (!targets.contains(e) || !(e instanceof EntityPlayer)) continue;
            targets.remove(e);
        }
        if (currentTarget >= attackList.size()) {
            currentTarget = 0;
        }
        for (Object o : this.mc.theWorld.loadedEntityList) {
            e = (Entity)o;
            if (this.isValidTarget(e) && !attackList.contains(e)) {
                attackList.add(e);
            }
            if (this.isValidTarget(e) || !attackList.contains(e)) continue;
            attackList.remove(e);
        }
        this.sortTargets();
        if (this.mc.thePlayer != null && attackList.size() != 0 && attackList.get(currentTarget) != null && this.isValidTarget(attackList.get(currentTarget)) && this.mc.thePlayer.isUsingItem() && this.mc.thePlayer.getCurrentEquippedItem().getItem() != null && this.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            int bowCurrentCharge = this.mc.thePlayer.getItemInUseDuration();
            float bowVelocity = (float)bowCurrentCharge / 20.0f;
            bowVelocity = (bowVelocity * bowVelocity + bowVelocity * 2.0f) / 3.0f;
            bowVelocity = MathHelper.clamp_float(bowVelocity, 0.0f, 1.0f);
            double v = bowVelocity * 3.0f;
            double g = 0.05000000074505806;
            if ((double)bowVelocity < 0.1) {
                return;
            }
            if (bowVelocity > 1.0f) {
                bowVelocity = 1.0f;
            }
            double xDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - this.mc.thePlayer.posX + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosX) * (double)(bowVelocity * 10.0f);
            double zDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - this.mc.thePlayer.posZ + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosZ) * (double)(bowVelocity * 10.0f);
            double trajectoryXZ = Math.sqrt(xDistance * xDistance + zDistance * zDistance);
            float trajectoryTheta90 = (float)(Math.atan2(zDistance, xDistance) * 180.0 / 3.141592653589793) - 90.0f;
            float bowTrajectory = (float)((double)((float)(- Math.toDegrees(this.getLaunchAngle(attackList.get(currentTarget), v, g)))) - 3.8);
            if (trajectoryTheta90 <= 360.0f && bowTrajectory <= 360.0f) {
                if (this.lockView.getValue().booleanValue()) {
                    this.mc.thePlayer.rotationYaw = trajectoryTheta90;
                    this.mc.thePlayer.rotationPitch = bowTrajectory;
                } else {
                    pre.setYaw(trajectoryTheta90);
                    pre.setPitch(bowTrajectory);
                }
            }
        }
        currentTarget ++;
    }

    public void sortTargets() {
        attackList.sort((ent1, ent2) -> {
            double d2;
            double d1 = this.mc.thePlayer.getDistanceToEntity((Entity)ent1);
            return d1 < (d2 = (double)this.mc.thePlayer.getDistanceToEntity((Entity)ent2)) ? -1 : (d1 == d2 ? 0 : 1);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targets.clear();
        attackList.clear();
        currentTarget = 0;
    }

    private float getLaunchAngle(Entity targetEntity, double v, double g) {
        double yDif = targetEntity.posY + (double)(targetEntity.getEyeHeight() / 2.0f) - (this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight());
        double xDif = targetEntity.posX - this.mc.thePlayer.posX;
        double zDif = targetEntity.posZ - this.mc.thePlayer.posZ;
        double xCoord = Math.sqrt(xDif * xDif + zDif * zDif);
        return this.theta(v + 2.0, g, xCoord, yDif);
    }

    private float theta(double v, double g, double x, double y) {
        double yv = 2.0 * y * (v * v);
        double gx = g * (x * x);
        double g2 = g * (gx + yv);
        double insqrt = v * v * v * v - g2;
        double sqrt = Math.sqrt(insqrt);
        double numerator = v * v + sqrt;
        double numerator2 = v * v - sqrt;
        double atan1 = Math.atan2(numerator, g * x);
        double atan2 = Math.atan2(numerator2, g * x);
        return (float)Math.min(atan1, atan2);
    }
}

