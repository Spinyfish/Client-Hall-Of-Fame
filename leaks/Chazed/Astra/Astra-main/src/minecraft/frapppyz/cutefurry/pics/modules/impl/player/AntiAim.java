package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Motion;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.RotUtil;

public class AntiAim extends Mod {

    private Mode mode = new Mode("Mode", "Spin", "Spin", "Random", "Jitter");
    private Boolean clientside = new Boolean("Only Client", false);
    private int pitch;
    private int yaw;
    public AntiAim() {
        super("AntiAim", "Aims the Anti", 0, Category.PLAYER);
        addSettings(mode, clientside);
    }

    public int getSpinRot(){
        return yaw + 45;
    }

    public int getSpinRot2(){
        return yaw + 10;
    }

    public int getRandRot(){
        return (int) (Math.random()*280);
    }

    public void onEvent(Event e){
        if(e instanceof Motion){
            if(mode.is("Spin")){
                yaw = getSpinRot();
                pitch = 70;
                if(yaw >= 360){
                    yaw = 0;
                }
            }else if(mode.is("Random")){
                yaw = getRandRot();
                pitch = getRandRot();
            }else if(mode.is("Jitter")){
                yaw = getSpinRot2();
                pitch = 70;
                if(Math.random() > 0.6){
                    yaw = (int) (mc.thePlayer.rotationYaw-152 - Math.random()*60);
                }
            }


            if(!clientside.isToggled()){
                ((Motion) e).setYaw(yaw);
                ((Motion) e).setPitch(pitch);
            }
            RotUtil.renderRots(yaw, pitch);

        }

    }
}
