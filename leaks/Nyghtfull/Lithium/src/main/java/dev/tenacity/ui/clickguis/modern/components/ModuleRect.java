package dev.tenacity.ui.clickguis.modern.components;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.animations.impl.ElasticAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ModuleRect extends Component {
    @Getter
    @Setter
    private int searchScore = 0;
    public final Module module;
    public Animation settingAnimation;
    public Animation hoverDescriptionAnimation;
    public Module binding;
    public float rectOffset = 0;
    public boolean rightClicked = false;
    public boolean drawSettingThing = false;
    private Animation rectScaleAnimation;
    private Animation checkScaleAnimation;

    public ModuleRect(Module module) {
        this.module = module;
    }

    @Override
    public void initGui() {
        hoverDescriptionAnimation = new DecelerateAnimation(250, 1);
        hoverDescriptionAnimation.setDirection(Direction.BACKWARDS);

        settingAnimation = new DecelerateAnimation(400, 1);
        settingAnimation.setDirection(Direction.BACKWARDS);

        rectScaleAnimation = new DecelerateAnimation(250, 1);
        rectScaleAnimation.setDirection(Direction.BACKWARDS);

        checkScaleAnimation = new ElasticAnimation(550, 1, 3.8f, 2, false);
        checkScaleAnimation.setDirection(Direction.BACKWARDS);

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (binding != null) {
            if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE)
                binding.getKeybind().setCode(Keyboard.KEY_NONE);
            else
                binding.getKeybind().setCode(keyCode);
            binding = null;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(x + .5f, y + .5f, rectWidth - 1, 34, 5, new Color(47, 49, 54));
        //  RenderUtil.renderRoundedRect(x, y, rectWidth, 35, 5, new Color(47, 49, 54).getRGB());

        if(rectScaleAnimation == null){
            System.out.println("CRAZXy" + " " + module.getName());
        }
        rectScaleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        checkScaleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        checkScaleAnimation.setDuration(module.isEnabled() ? 550 : 250);

        Color clickModColor = Color.WHITE;
        Color clickModColor2 = Color.WHITE;
        HUDMod hudMod = Tenacity.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        Pair<Color, Color> colors = HUDMod.getClientColors();


        RoundedUtil.drawRound(x + .5f, y + .5f, 34, 34, 5, new Color(68, 71, 78));
        RenderUtil.drawGoodCircle(x + 35 / 2f, y + 35 / 2f, 5, new Color(47, 49, 54).getRGB());

        float rectScale = rectScaleAnimation.getOutput().floatValue();

        GL11.glPushMatrix();
        GL11.glTranslatef(x + 17, y + 17, 0);
        GL11.glScaled(rectScale, rectScale, 0);
        GL11.glTranslatef(-(x + 17), -(y + 17), 0);
        GL11.glEnable(GL11.GL_BLEND);
        //  mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/gradient.png"));
        //Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 35, 35, 35, 35);
        float width = 35;
        float height = 35;
        RoundedUtil.drawGradientCornerLR(x + .5f, y + .5f, width - 1, height - 1, 5,
                ColorUtil.applyOpacity(colors.getFirst(), rectScale),
                ColorUtil.applyOpacity(colors.getSecond(), rectScale));
        GL11.glPopMatrix();

        float textX = (float) (x + (18 - FontUtil.iconFont35.getStringWidth(FontUtil.CHECKMARK) / 2f));
        float textY = (float) (y + 18.5 - FontUtil.iconFont35.getHeight() / 2f);
        GL11.glPushMatrix();
        GL11.glTranslatef(textX + 9, textY + 9, 0);
        GL11.glScaled(Math.max(0, checkScaleAnimation.getOutput().floatValue()), Math.max(0, checkScaleAnimation.getOutput().floatValue()), 0);
        GL11.glTranslatef(-(textX + 9), -(textY + 9), 0);

        FontUtil.iconFont35.drawSmoothString(FontUtil.CHECKMARK, textX, textY, ColorUtil.applyOpacity(-1, (float) checkScaleAnimation.getOutput().floatValue()));
        GL11.glPopMatrix();

        boolean hoverModule = HoveringUtil.isHovering(x, y, rectWidth, 35, mouseX, mouseY);
        hoverDescriptionAnimation.setDirection(hoverModule ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverDescriptionAnimation.setDuration(hoverModule ? 300 : 400);

        GlStateManager.color(1, 1, 1, 1);
        float xStart = (float) (x + 55 + lithiumFont24.getStringWidth(module.getName()));
        float yVal = y + 35 / 2f - lithiumFont18.getHeight() / 2f;
        if (module.getCategory().equals(Category.SCRIPTS)) yVal -= 6;
        if (binding != module && (!hoverDescriptionAnimation.isDone() || hoverDescriptionAnimation.finished(Direction.FORWARDS))) {
            float hover = hoverDescriptionAnimation.getOutput().floatValue();
            float descWidth = 305 - ((55 + lithiumFont24.getStringWidth(module.getName())) + 15);

            lithiumFont18.drawWrappedText(module.getDescription(), xStart, yVal,
                    new Color(128, 134, 141, (int) (255 * hover)).getRGB(), descWidth, 3);
        } else if (binding == module){
            lithiumFont18.drawString(
                    "Currently bound to " + Keyboard.getKeyName(module.getKeybind().getCode()),
                    xStart, yVal, new Color(128, 134, 141).getRGB());
        }


        if (module.getCategory().equals(Category.SCRIPTS)) {
            lithiumFont24.drawString(module.getName(), x + 42, y + (35 / 2f - lithiumFont24.getHeight() / 2f) - 6, -1);
            lithiumFont18.drawString(module.getAuthor(), x + 42, y + (35 / 2f - lithiumFont24.getHeight() / 2f) + 9,
                    new Color(255, 65, 65).getRGB());
        } else {
            lithiumFont24.drawString(module.getName(), x + 42, y + 35 / 2f - lithiumFont24.getHeight() / 2f, -1);
        }
        settingAnimation.setDirection(drawSettingThing ? Direction.FORWARDS : Direction.BACKWARDS);


        int interpolateColorr = ColorUtil.interpolateColorsBackAndForth(40, 1, colors.getFirst(), colors.getSecond(), false).getRGB();

        RoundedUtil.drawRound(x + rectWidth - 14.5f, y + .5f, 14, 34, 5,
                ColorUtil.interpolateColorC(new Color(47, 49, 54), new Color(interpolateColorr), (float) settingAnimation.getOutput().floatValue()));

        RenderUtil.drawGoodCircle(x + rectWidth - 7.5, y + 7.5, 2.5f, -1);
        RenderUtil.drawGoodCircle(x + rectWidth - 7.5, y + 17.5, 2.5f, -1);
        RenderUtil.drawGoodCircle(x + rectWidth - 7.5, y + 27.5, 2.5f, -1);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (HoveringUtil.isHovering(x, y, rectWidth, 35, mouseX, mouseY)) {
            if (y > (bigRecty + rectOffset) && y < bigRecty + 255) {
                if (button == 0) {
                    module.toggleSilent();
                }
                if (button == 2) {
                    binding = module;
                }
                rightClicked = button == 1;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            binding = null;
        }
    }
}
