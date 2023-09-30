package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;
import Reality.Realii.utils.math.AnimationUtils;

public class ChatLine {
    /**
     * GUI Update Counter value this Line was created at
     */
    private final int updateCounterCreated;
    private final IChatComponent lineString;
    public AnimationUtils animationUtils = new AnimationUtils();
    public AnimationUtils animationUtils2 = new AnimationUtils();

    public float x, y;

    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;

    public ChatLine(int p_i45000_1_, IChatComponent p_i45000_2_, int p_i45000_3_) {
        this.lineString = p_i45000_2_;
        this.updateCounterCreated = p_i45000_1_;
        this.chatLineID = p_i45000_3_;
    }

    public IChatComponent getChatComponent() {
        return this.lineString;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getChatLineID() {
        return this.chatLineID;
    }
}
