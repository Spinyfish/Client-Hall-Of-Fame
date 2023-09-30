package felix.module.impl.combat;

import java.util.ArrayList;
import java.util.List;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.util.player.RotationUtils;
import felix.value.impl.BooleanValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;

public class BowAimbot extends Module {

    boolean send, isFiring;
    
    private BooleanValue silent = new BooleanValue("Silent", true);
    
    public static EntityLivingBase target;
    
    public BowAimbot() {
        super("BowAimbot", 0, ModuleCategory.COMBAT);
        addValues(silent);
    }

    @Override
    public void onDisable(){
    	super.onDisable();
    	target = null;
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
    
    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	if (event.isPre()) {
    		target = getTarg();
    		if(shouldAim()){
    			if (target != null) {
    				float[] rotations = RotationUtils.getBowAngles(target);
    				boolean silent = this.silent.isEnabled();
    				if(silent){
    					event.setYaw(rotations[0]);
    					event.setPitch(rotations[1]);
    				} 
    				else {
    					mc.thePlayer.rotationYaw = rotations[0];
    					mc.thePlayer.rotationPitch = rotations[1];
                    }
                }
            }
        }
    }
    
    public boolean shouldAim(){
    	if(mc.thePlayer.inventory.getCurrentItem() == null || !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow))
    		return false;
    	if(Client.INSTANCE.getModuleManager().getModule(FastBow.class).isEnabled() && mc.gameSettings.keyBindUseItem.pressed)
    		return true;
    	if(mc.thePlayer.isUsingItem())
            return true;
    	return false;
    }
    
    private EntityLivingBase getTarg() {
        List<EntityLivingBase> loaded = new ArrayList();
        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) o;
                if (ent instanceof EntityPlayer && ent != mc.thePlayer && mc.thePlayer.canEntityBeSeen(ent) && mc.thePlayer.getDistanceToEntity(ent) < 65 && !PlayerUtil.isOnSameTeam(ent)) {
                    loaded.add(ent);
                }
            }
        }
        if (loaded.isEmpty()) {
            return null;
        }
        loaded.sort((o1, o2) -> {
            float[] rot1 = RotationUtils.getNeededRotations(o1);
            float[] rot2 = RotationUtils.getNeededRotations(o2);
            return (int) ((RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot1[0])
                    + RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot1[1]))
                    - (RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot2[0])
                    + RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot2[1])));
        });
        EntityLivingBase target = loaded.get(0);
        return target;
    }
}
