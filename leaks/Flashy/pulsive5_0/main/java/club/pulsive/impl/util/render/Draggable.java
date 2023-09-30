package club.pulsive.impl.util.render;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.impl.movement.Sprint;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.impl.MainAnimations;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Draggable implements MinecraftUtil {
    @Expose
    @SerializedName("x")
    private float x;
    
    @Expose
    @SerializedName("y")
    private float y;
    private float initialX, initialY, startX, startY, width, height;
    private boolean dragging;

    @Expose
    @SerializedName("name")
    private String name;
    
    public Animation hoverAnimation = new MainAnimations(250, 1, Direction.BACKWARDS);
    
    public Module module;
    
    public Draggable(Module module, String name, float initialX, float initialY) {
        this.module = module;
        this.name = name;
        this.x = initialX;
        this.y = initialY;
        this.initialX = initialX;
        this.initialY = initialY;
    }
    
    
    public final void onDraw(int mouseX, int mouseY) {
        boolean hovering = RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
        if(!this.module.isToggled()) return;
        if (dragging) {
            x = (mouseX - startX);
            y = (mouseY - startY);
        }
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!hoverAnimation.isDone() || hoverAnimation.finished(Direction.FORWARDS)) {
            RoundedUtil.drawRoundedOutline(x, y, x + width, y + height, 2, 1.5f, RenderUtil.applyOpacity(Color.WHITE, (float) hoverAnimation.getOutput()).getRGB());
//            RenderUtils.drawBorderedRoundedRect(x, y, x + width, y + height, 4, 0.5f,
//                    RenderUtil.applyOpacity(Color.WHITE, (float) hoverAnimation.getOutput()).getRGB(), new Color(0, 0, 0, 0).getRGB());
        }
    }

    public final void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
        if(!this.module.isToggled()) return;
        if (canDrag) {
            dragging = true;
            startX = (int) (mouseX - x);
            startY = (int) (mouseY - y);
        }
    }

    public final void onRelease(int button) {
        if(!this.module.isToggled()) return;
        if (button == 0) dragging = false;
    }

}