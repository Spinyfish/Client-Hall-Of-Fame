/* November.lol © 2023 */
package lol.november.feature.hud.impl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import lol.november.November;
import lol.november.feature.hud.HUDElement;
import lol.november.feature.hud.Register;
import lol.november.feature.module.impl.combat.KillAuraModule;
import lol.november.utility.render.RenderUtils;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(value = "TargetHUD", state = true)
public class TargetHUDElement extends HUDElement {

  /**
   * The {@link KillAuraModule} instance
   */
  private static final KillAuraModule KILL_AURA = November
    .instance()
    .modules()
    .get(KillAuraModule.class);

  private final Animation animation = new Animation(
    Easing.CUBIC_IN_OUT,
    200.0,
    false
  );

  private EntityLivingBase lastAttackTarget;

  @Override
  public void render(ScaledResolution resolution, float partialTicks) {
    double x = resolution.getScaledWidth_double() - 300.0;
    double y = (resolution.getScaledHeight_double() / 2.0) + 80;
    double width = 145;
    double height = 45;

    //    boolean animateState =
    //      (lastAttackTarget != null && !animation.getState()) &&
    //        KILL_AURA.toggled();
    //    if (animation.getState() != animateState) {
    //      animation.setState(animateState);
    //    }

    animation.setState(true);

    double animationFactor = 1.0 - animation.getFactor();
    if (animationFactor <= 0.0) return;

    EntityLivingBase target = KILL_AURA.getAttackTarget();
    if (target == null && mc.currentScreen instanceof GuiChat) {
      target = mc.thePlayer;
    }

    if (target == null) return;
    lastAttackTarget = target;

    glPushMatrix();

    glTranslated(
      (x + width / 2.0) * animationFactor,
      (y + height / 2.0) * animationFactor,
      0.0
    );
    glScaled(animationFactor - 1.0, animationFactor - 1.0, 0.0);

    RenderUtils.roundRect(x, y, width, height, 5.0f, 0xFF000000);

    glPopMatrix();
  }
}
