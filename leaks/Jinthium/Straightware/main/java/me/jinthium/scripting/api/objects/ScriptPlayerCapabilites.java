package me.jinthium.scripting.api.objects;

import net.minecraft.entity.player.PlayerCapabilities;

public class ScriptPlayerCapabilites {

    public PlayerCapabilities getActualAbilites() {
        PlayerCapabilities playerCapabilities = new PlayerCapabilities();

        playerCapabilities.setFlySpeed(flySpeed);
        playerCapabilities.setPlayerWalkSpeed(walkSpeed);
        playerCapabilities.allowEdit = allowEdit;
        playerCapabilities.disableDamage = disableDamage;
        playerCapabilities.isFlying = isFlying;
        playerCapabilities.allowFlying = allowFlying;
        playerCapabilities.isCreativeMode = isCreativeMode;
        return playerCapabilities;
    }



    /**
     * Disables player damage.
     */
    public boolean disableDamage;

    /**
     * Sets/indicates whether the player is flying.
     */
    public boolean isFlying;

    /**
     * whether or not to allow the player to fly when they double jump.
     */
    public boolean allowFlying;

    /**
     * Used to determine if creative mode is enabled, and therefore if items should be depleted on usage
     */
    public boolean isCreativeMode;

    /**
     * Indicates whether the player is allowed to modify the surroundings
     */
    public boolean allowEdit = true;
    private float flySpeed = 0.05F;
    private float walkSpeed = 0.1F;

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float speed) {
        this.flySpeed = speed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setPlayerWalkSpeed(float speed) {
        this.walkSpeed = speed;
    }

}
