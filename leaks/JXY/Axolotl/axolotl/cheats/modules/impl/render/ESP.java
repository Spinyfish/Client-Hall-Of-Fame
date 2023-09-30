package axolotl.cheats.modules.impl.render;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.RenderEvent;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.BooleanSetting;
import axolotl.cheats.settings.SpecialSetting;
import axolotl.cheats.settings.SpecialSettings;
import axolotl.util.RenderUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class ESP extends Module {

    public BooleanSetting box = new BooleanSetting("Box", true), tracers = new BooleanSetting("Tracers", true);
    public BooleanSetting ESPSelf = new BooleanSetting("ESPSelf", false);
    public BooleanSetting mob = new BooleanSetting("Mobs", true);
    public BooleanSetting player = new BooleanSetting("Players", true);
    public BooleanSetting animal = new BooleanSetting("Animals", true);
    public BooleanSetting other = new BooleanSetting("Other", true);
    public BooleanSetting water = new BooleanSetting("Water", true);

    public ESP() {
        super("ESP", Category.RENDER, true);
        if(Math.random() < 0.08) {
            this.name = "UncodableESP";
        }
        if(Math.random() < 0.069) {
            this.name = "NefSexESP";
        }
        this.addSettings(box, tracers, ESPSelf, mob, player, animal, water, other);
    }

    public void onEvent(Event event) {

        if (!(event instanceof RenderEvent) || event.eventType != EventType.PRE) return;

        mc.gameSettings.viewBobbing = false;

        for (Object obj : mc.theWorld.loadedEntityList) {
            try {
                EntityLivingBase entity = (EntityLivingBase) obj;
                if (entity.isPotionActive(Potion.invisibility) || entity.isInvisible()) continue;
                if (entity instanceof EntityPlayer && player.isEnabled()) {
                    if (!ESPSelf.isEnabled() && obj == mc.thePlayer && player.isEnabled()) continue;
                    if (obj == mc.thePlayer) {
                        if (mc.gameSettings.thirdPersonView == 0) {
                            player(entity, false, false);
                        } else {
                            player(entity, false, true);
                        }
                    } else player(entity, true, true);
                } else if (entity instanceof EntityMob && mob.isEnabled()) {
                    render(1f, 1f, 0f, entity.width, entity.height, true, true, entity);
                } else if (entity instanceof EntityAnimal && animal.isEnabled()) {
                    render(0f, 1f, 0f, entity.width, entity.height, true, true, entity);
                } else if (entity instanceof EntityWaterMob && water.isEnabled()) {
                    render(0f, 0f, 1f, entity.width, entity.height, true, true, entity);
                } else if (other.isEnabled() && !(entity instanceof EntityAnimal) && !(entity instanceof EntityMob) && !(entity instanceof EntityPlayer) && !(entity instanceof EntityWaterMob)) {
                    render(0.5f, 0.5f, 0.5f, entity.width, entity.height, true, true, entity);
                }
            } catch (ClassCastException e) {
                continue;
            }
        }
    }

    public void player(EntityLivingBase entity, boolean t, boolean v)
    {
        render(1f, 0f, 0f, entity.width, entity.height, t, v, entity);
    }

    public void render(float red, float green, float blue, float width, float height, boolean t, boolean v, EntityLivingBase entity) {

        double x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
        double z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        if(box.isEnabled() && v)
            RenderUtils.drawEntityESP(x, y, z, width - (width / 4), height, red, green, blue, 0.2F, 0F, 0F, 0F, 1F, 1F);
        if(tracers.isEnabled() && t) {
            RenderUtils.drawTracerLine(x, y, z, red, green, blue, 0.45F, 1F);
        }
    }
}
