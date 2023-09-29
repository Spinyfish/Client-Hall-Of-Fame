package com.alan.clients.component.impl.hud;

import com.alan.clients.Client;
import com.alan.clients.component.Component;
import com.alan.clients.component.impl.hud.dragcomponent.api.Orientation;
import com.alan.clients.component.impl.hud.dragcomponent.api.Snap;
import com.alan.clients.module.Module;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.GuiClickEvent;
import com.alan.clients.newevent.impl.input.GuiMouseReleaseEvent;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.DragValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import util.time.StopWatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

import static com.alan.clients.util.animation.Easing.LINEAR;

public class DragComponent extends Component {

    private static DragValue selectedValue;
    private static Vector2d offset;
    private static final ArrayList<Module> modules = new ArrayList<>();
    private static final Animation animationAlpha = new Animation(LINEAR, 600);
    public static final StopWatch closeStopWatch = new StopWatch(), stopWatch = new StopWatch();
    private static final ResourceLocation image = new ResourceLocation("rise/icons/click.png");
    private static ScaledResolution lastScaledResolution;

    public static ArrayList<Snap> snaps = new ArrayList<>();
    public static Snap selected;

    public static void render(float partialTicks) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        boolean shouldRender = mc.currentScreen instanceof GuiChat;

        if (!shouldRender) {
            selectedValue = null;
        } else {
            closeStopWatch.reset();
        }

        animationAlpha.setEasing(LINEAR);
        animationAlpha.setDuration(300);
        animationAlpha.run(shouldRender ? 100 : 0);

        if (animationAlpha.getValue() <= 0 && closeStopWatch.finished(0)) {
            selectedValue = null;
            return;
        }

        modules.clear();
        Client.INSTANCE.getModuleManager().stream().filter(module ->
                        module.isEnabled() && module.getValues().stream().
                                anyMatch(value -> value instanceof DragValue)).
                forEach(modules::add);

        if (selectedValue != null) {

            final int mouseX = Mouse.getX() * width / mc.displayWidth;
            final int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
            final double positionX = mouseX + offset.x;
            final double positionY = mouseY + offset.y;

            selectedValue.targetPosition = new Vector2d(positionX, positionY);

            // Setup snapping
            snaps.clear();

            double edgeSnap = 4.5f;

            // Permanent snaps
            snaps.add(new Snap(width / 2f, 5, Orientation.HORIZONTAL, true, true, true));
            snaps.add(new Snap(height / 2f, 5, Orientation.VERTICAL, true, true, true));

            snaps.add(new Snap(height - edgeSnap, 5, Orientation.VERTICAL, false, false, true));
            snaps.add(new Snap(edgeSnap, 5, Orientation.VERTICAL, false, true, false));
            snaps.add(new Snap(width - edgeSnap, 5, Orientation.HORIZONTAL, false, false, true));
            snaps.add(new Snap(edgeSnap, 5, Orientation.HORIZONTAL, false, true, false));

            for (Module module : modules) {
                // Getting Position Value
                Optional<Value<?>> positionValues = module.getValues().stream().filter(value ->
                        value instanceof DragValue).findFirst();
                DragValue positionValue = ((DragValue) positionValues.get());

                if (positionValue == selectedValue) continue;

                snaps.add(new Snap(positionValue.position.x + positionValue.scale.x + edgeSnap, 5, Orientation.HORIZONTAL, false, true, false));
                snaps.add(new Snap(positionValue.position.x - edgeSnap, 5, Orientation.HORIZONTAL, false, false, true));

                snaps.add(new Snap(positionValue.position.y, 5, Orientation.VERTICAL, false, false, true));
                snaps.add(new Snap(positionValue.position.y + positionValue.scale.y, 5, Orientation.VERTICAL, false, true, false));
            }

            double closest;
            selected = null;

            for (Snap snap : snaps) {
                switch (snap.orientation) {
                    case VERTICAL:
                        closest = Double.MAX_VALUE;

                        for (double y = -selectedValue.scale.y; y <= 0; y += selectedValue.scale.y / 2f) {
                            if ((y == -selectedValue.scale.y / 2 && !snap.center) || (y == -selectedValue.scale.y && !snap.left) || (y == 0 && !snap.right)) {
                                continue;
                            }

                            double distance = Math.abs(selectedValue.targetPosition.y - (snap.position + y));

                            if (distance < snap.distance && distance < closest) {
                                closest = distance;
                                selectedValue.targetPosition.y = snap.position + y;
                                selected = snap;
                            }
                        }
                        break;

                    case HORIZONTAL:
                        closest = Double.MAX_VALUE;
                        for (double x = -selectedValue.scale.x; x <= 0; x += selectedValue.scale.x / 2f) {
                            if ((x == -selectedValue.scale.x / 2 && !snap.center) || (x == -selectedValue.scale.x && !snap.left) || (x == 0 && !snap.right)) {
                                continue;
                            }

                            double distance = Math.abs(selectedValue.targetPosition.x - (snap.position + x));

                            if (distance < snap.distance && distance < closest) {
                                closest = distance;
                                selectedValue.targetPosition.x = snap.position + x;
                                selected = snap;
                            }
                        }
                        break;
                }
            }

            // Rendering snap
            Color color = ColorUtil.withAlpha(Color.WHITE, 60);
            if (selected != null) {
                switch (selected.orientation) {
                    case VERTICAL:
                        RenderUtil.rectangle(0, selected.position, scaledResolution.getScaledWidth(), 0.5, color);
                        break;

                    case HORIZONTAL:
                        RenderUtil.rectangle(selected.position, 0, 0.5, scaledResolution.getScaledHeight(), color);
                        break;
                }
            }

            // Validating position
            for (Module module : modules) {
                // Getting Position Value
                Optional<Value<?>> positionValues = module.getValues().stream().filter(value ->
                        value instanceof DragValue).findFirst();
                DragValue positionValue = ((DragValue) positionValues.get());

                int offset = 4;

                positionValue.position.x = Math.max(offset, positionValue.position.x);
                positionValue.position.x = Math.min(width - positionValue.scale.x - offset, positionValue.position.x);

                positionValue.position.y = Math.max(offset, positionValue.position.y);
                positionValue.position.y = Math.min(height - positionValue.scale.y - offset, positionValue.position.y);

                positionValue.targetPosition.x = Math.max(offset, positionValue.targetPosition.x);
                positionValue.targetPosition.x = Math.min(width - positionValue.scale.x - offset, positionValue.targetPosition.x);

                positionValue.targetPosition.y = Math.max(offset, positionValue.targetPosition.y);
                positionValue.targetPosition.y = Math.min(height - positionValue.scale.y - offset, positionValue.targetPosition.y);

                positionValue.position = positionValue.targetPosition;
            }


        }

        stopWatch.reset();
    }

    @EventLink()
    public final Listener<GuiClickEvent> onGuiClick = event -> {
        if (event.getMouseButton() != 0) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            for (final Module module : modules) {
                for (final Value<?> value : module.getValues()) {
                    if (value instanceof DragValue) {
                        final DragValue positionValue = (DragValue) value;
                        final Vector2d position = positionValue.position;
                        final Vector2d scale = positionValue.scale;
                        final float mouseX = event.getMouseX();
                        final float mouseY = event.getMouseY();

                        if (!positionValue.structure && GUIUtil.mouseOver(position, scale, mouseX, mouseY)) {
                            selectedValue = positionValue;

                            offset = new Vector2d(position.x - mouseX, position.y - mouseY);
                        }
                    }
                }
            }
        }
    };

    @EventLink()
    public final Listener<GuiMouseReleaseEvent> onGuiMouseRelease = event -> {
        selectedValue = null;
    };
}