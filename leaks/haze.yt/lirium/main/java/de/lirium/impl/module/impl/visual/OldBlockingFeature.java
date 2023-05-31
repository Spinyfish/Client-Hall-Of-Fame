package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.RenderItemEvent;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

@ModuleFeature.Info(name = "Old Blocking", description = "Bring back the old blocking animation", category = ModuleFeature.Category.VISUAL)
public class OldBlockingFeature extends ModuleFeature {

    @Value(name = "Animation")
    ComboBox<String> animation = new ComboBox<>("Jello", new String[]{"1.7", "1.8", "Assassin", "None"});

    @Value(name = "Fake Block")
    final CheckBox fakeBlock = new CheckBox(false);

    @Value(name = "All Items")
    final CheckBox allItems = new CheckBox(false);

    @EventHandler
    public final Listener<RenderItemEvent> renderItemEventListener = this::renderItem;

    private void renderItem(RenderItemEvent e) {
        if (e.getStack().getItemUseAction().equals(EnumAction.BLOCK))
            e.setCancelled(true);
        if ((allItems.getValue() || e.getStack().getItem() instanceof ItemSword) && ((e.getPlayer().getHeldItem(e.getHand() == EnumHand.MAIN_HAND ?
                EnumHand.OFF_HAND : EnumHand.MAIN_HAND).getItemUseAction().equals(EnumAction.BLOCK) && e.getPlayer().isHandActive()) || (fakeBlock.getValue() && e.getSwingProgress() != 0.0F))) {
            float f = MathHelper.sin(MathHelper.sqrt(e.getSwingProgress()) * (float) Math.PI);
            float f1 = MathHelper.sin(e.getSwingProgress() * e.getSwingProgress() * (float) Math.PI);

            switch (animation.getValue()) {
                case "1.7":
                    GlStateManager.translate(0.2, -0.1,-0.2);
                    break;
            }

            if (!animation.getValue().equals("None")) {
                e.transformSideFirstPerson(e.getEnumHandSide(), 0.0F);

                if (!e.isRightHand())
                    GlStateManager.rotate(180, 0, 1, 0);

                e.block();
            }
            switch (animation.getValue()) {
                case "None":
                    e.setCancelled(true);
                    final boolean flag1 = e.getEnumHandSide() == EnumHandSide.RIGHT;
                    final float f2 = -0.4F * MathHelper.sin(MathHelper.sqrt(e.getSwingProgress()) * (float)Math.PI);
                    final float f3 = 0.2F * MathHelper.sin(MathHelper.sqrt(e.getSwingProgress()) * ((float)Math.PI * 2F));
                    final float f4 = -0.2F * MathHelper.sin(e.getSwingProgress() * (float)Math.PI);
                    final int i = flag1 ? 1 : -1;
                    GlStateManager.translate((float)i * f2, f3, f2);
                    e.transformSideFirstPerson(e.getEnumHandSide(), e.getEquipProgress());
                    e.transformFirstPerson(e.getEnumHandSide(), e.getSwingProgress());
                    mc.getItemRenderer().renderItemSide(e.getPlayer(), e.getStack(), flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
                    break;
                case "Jello":
                    GlStateManager.translate(0.1, 0.0, 0.0);
                    GlStateManager.rotate(f * 30.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(-0.1, 0.0, 0.0);
                    break;
                case "1.8":
                    GlStateManager.translate(-0.025, 0.15, -0.06);
                    GlStateManager.rotate(15, 0, 1, 0);
                    GlStateManager.translate(0, 0, -f * 0.01);
                    break;
                case "1.7":
                    GlStateManager.rotate(f1 * 50.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f * -20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 1.0F);
                    GlStateManager.rotate(f * -80.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case "Assassin":
                    GlStateManager.rotate((f1 != 0 ? 90.0F : 0) + f1, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f * 90, 0.0F, 1.0F, 0.0F);
                    if (f1 != 0)
                        GlStateManager.rotate(90, 0.0F, 0.0F, 1.0F);
                    break;
            }

            e.renderItemSide(e.getPlayer(), e.getStack(), e.isRightHand() ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !e.isRightHand());
            e.setCancelled(true);
        }
    }

}