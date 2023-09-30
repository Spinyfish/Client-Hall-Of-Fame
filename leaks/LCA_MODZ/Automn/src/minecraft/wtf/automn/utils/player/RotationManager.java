package wtf.automn.utils.player;

import wtf.automn.module.Module;
import wtf.automn.utils.interfaces.MM;

import java.util.HashMap;

public class RotationManager implements wtf.automn.utils.interfaces.MC, MM {
    private float[] finalRotation = new float[2];
    private float[] lastRotation = new float[2];
    private int priority = 0;
    private HashMap<Module, Integer> moduleHashMap = new HashMap<>();

    public void clearRotations(float[] rots) {
        priority = 100;
        this.lastRotation = finalRotation;
        finalRotation = rots;
        moduleHashMap.clear();
    }

    public void updateRotations() {
        mc.thePlayer.rotationYaw = finalRotation[0];
        mc.thePlayer.rotationPitch = finalRotation[1];
    }

    public boolean shouldRotate(Module module){
        return moduleHashMap.containsKey(module) && priority == this.moduleHashMap.get(module);
    }

    public void setRotation(float[] rotation, int priority, Module module) {
        this.moduleHashMap.put(module, priority);
        if (this.priority > priority) {
            finalRotation = rotation;
            this.priority = priority;
        }
    }

    public void setYawRotation(float yaw, int priority, Module module) {
        this.moduleHashMap.put(module, priority);
        if (this.priority > priority) {
            finalRotation = new float[] {yaw, finalRotation[1]};
            this.priority = priority;
        }
    }

    public void setPitchRotation(float pitch, int priority, Module module) {
        this.moduleHashMap.put(module, priority);
        if (this.priority > priority) {
            finalRotation = new float[] {finalRotation[0], pitch};
            this.priority = priority;
        }
    }

    public float[] getFinalRotation() {
        return finalRotation;
    }

    public float[] getLastRotation() {
        return lastRotation;
    }
}
