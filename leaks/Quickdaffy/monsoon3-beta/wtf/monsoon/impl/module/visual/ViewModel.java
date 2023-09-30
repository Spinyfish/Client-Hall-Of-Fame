/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventFOVUpdate;
import wtf.monsoon.impl.event.EventScaleItem;
import wtf.monsoon.impl.event.EventTransformItem;

public class ViewModel
extends Module {
    public final Setting<Boolean> combineScale = new Setting<Boolean>("Combine Scale", true).describedBy("Combine X, Y, and Z scale into one.");
    public final Setting<Double> scaleCombine = new Setting<Double>("Scale", 1.0).minimum(0.1).maximum(5.0).incrementation(0.05).describedBy("The scale of the item.").visibleWhen(this.combineScale::getValue);
    public final Setting<Double> scaleX = new Setting<Double>("X Scale", 1.0).minimum(0.1).maximum(5.0).incrementation(0.05).describedBy("The scale of the item on the X axis.").visibleWhen(() -> this.combineScale.getValue() == false);
    public final Setting<Double> scaleY = new Setting<Double>("Y Scale", 1.0).minimum(0.1).maximum(5.0).incrementation(0.05).describedBy("The scale of the item on the Y axis.").visibleWhen(() -> this.combineScale.getValue() == false);
    public final Setting<Double> scaleZ = new Setting<Double>("Z Scale", 1.0).minimum(0.1).maximum(5.0).incrementation(0.05).describedBy("The scale of the item on the Z axis.").visibleWhen(() -> this.combineScale.getValue() == false);
    public final Setting<Boolean> modifyPosition = new Setting<Boolean>("Modify Position", false).describedBy("Whether to modify the position of the item.");
    public final Setting<Double> posX = new Setting<Double>("Pos X", 0.56).minimum(-2.0).maximum(2.0).incrementation(0.01).describedBy("The position of the item on the X axis.").visibleWhen(this.modifyPosition::getValue);
    public final Setting<Double> posY = new Setting<Double>("Pos Y", -0.52).minimum(-2.0).maximum(2.0).incrementation(0.01).describedBy("The position of the item on the Y axis.").visibleWhen(this.modifyPosition::getValue);
    public final Setting<Double> posZ = new Setting<Double>("Pos Z", -0.72).minimum(-2.0).maximum(2.0).incrementation(0.01).describedBy("The position of the item on the Z axis.").visibleWhen(this.modifyPosition::getValue);
    public final Setting<Boolean> enableItemFOV = new Setting<Boolean>("Enable Item FOV", false).describedBy("Whether to enable item FOV.");
    public final Setting<Double> itemFOV = new Setting<Double>("Item FOV", 110.0).minimum(70.0).maximum(130.0).incrementation(1.0).describedBy("Your item FOV.").visibleWhen(this.enableItemFOV::getValue);
    public final Setting<Boolean> onlyEmptyHand = new Setting<Boolean>("Only When Empty Handed", false).describedBy("Whether to only change the FOV when your hand is empty.").visibleWhen(this.enableItemFOV::getValue);
    @EventLink
    private final Listener<EventFOVUpdate> eventFOVUpdateListener = e -> {
        if (this.enableItemFOV.getValue().booleanValue() && (this.mc.thePlayer.getHeldItem() == null || !this.onlyEmptyHand.getValue().booleanValue())) {
            e.setNewFOV((float)((double)e.getFov() + (this.itemFOV.getValue() - (double)e.getFov())));
        }
    };
    @EventLink
    private final Listener<EventTransformItem> eventRenderItemListener = e -> {
        if (this.modifyPosition.getValue().booleanValue()) {
            e.setPosX(this.posX.getValue().floatValue());
            e.setPosY(this.posY.getValue().floatValue());
            e.setPosZ(this.posZ.getValue().floatValue());
        }
    };
    @EventLink
    private final Listener<EventScaleItem> eventScaleItemListener = e -> {
        if (this.combineScale.getValue().booleanValue()) {
            e.setScaleX(e.getScaleX() * this.scaleCombine.getValue().floatValue());
            e.setScaleY(e.getScaleY() * this.scaleCombine.getValue().floatValue());
            e.setScaleZ(e.getScaleZ() * this.scaleCombine.getValue().floatValue());
        } else {
            e.setScaleX(e.getScaleX() * this.scaleX.getValue().floatValue());
            e.setScaleY(e.getScaleY() * this.scaleY.getValue().floatValue());
            e.setScaleZ(e.getScaleZ() * this.scaleZ.getValue().floatValue());
        }
    };

    public ViewModel() {
        super("View Model", "Change the view model of the item.", Category.VISUAL);
    }
}

