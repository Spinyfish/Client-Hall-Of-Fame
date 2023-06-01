package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;

public class Antibot extends Mod {
    Mode mode = new Mode("Mode", "Sparky", "Sparky");
    public static ArrayList<EntityLivingBase> bots = new ArrayList<>();
    public Antibot() {
        super("Antibot", "Detects killaura bots and says byebye", 0, Category.COMBAT);
        addSettings(mode);
    }

    public void onEvent(Event e){
        if(e instanceof Update){
                if(mode.is("Sparky")){
                    if(mc.thePlayer.ticksExisted <= 20){
                        bots.clear();
                    }
                    if(mc.thePlayer.ticksExisted % 5 == 0) {
                        for(EntityLivingBase ent : mc.theWorld.playerEntities) {

                            if(ent.everTouchedGround || (ent.isInvisible() && ent.ticksExisted < 20) || ent.getName().equals(mc.thePlayer.getName())) {
                                if(!bots.contains(ent)){
                                    bots.add(ent);
                                    Wrapper.getLogger().addChat(ent.getName() + " - everTouchedGround: " + ent.everTouchedGround + " - invisOnSpawn: " + (ent.ticksExisted < 20 && ent.isInvisible()));
                                }

                            }


                        }
                    }
            }
        }
    }
}
