package com.alan.clients.module.impl.render;

import com.alan.clients.Client;
import com.alan.clients.Type;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.render.interfaces.ModernInterface;
import com.alan.clients.module.impl.render.interfaces.ModuleComponent;
import com.alan.clients.module.impl.render.interfaces.WurstInterface;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import lombok.Getter;
import lombok.Setter;
import util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Getter
@Setter
@ModuleInfo(name = "module.render.interface.name", description = "module.render.interface.description", category = Category.RENDER, autoEnabled = true)
public final class Interface extends Module {

    private final ModeValue mode = new ModeValue("Mode", this, () -> Client.CLIENT_TYPE != Type.RISE) {{
        add(new ModernInterface("Modern", (Interface) this.getParent()));
        add(new WurstInterface("Wurst", (Interface) this.getParent()));
        setDefault("Modern");
    }};

    private final ModeValue modulesToShow = new ModeValue("Modules to Show", this, () -> Client.CLIENT_TYPE != Type.RISE) {{
        add(new SubMode("All"));
        add(new SubMode("Exclude render"));
        add(new SubMode("Only bound"));
        setDefault("Exclude render");
    }};

    public final BooleanValue limitChatWidth = new BooleanValue("Limit Chat Width", this, false);
    public final BooleanValue smoothHotBar = new BooleanValue("Smooth Hot Bar", this, true);

    public BooleanValue suffix = new BooleanValue("Suffix", this, true, () -> Client.CLIENT_TYPE != Type.RISE);
    public BooleanValue lowercase = new BooleanValue("Lowercase", this, false, () -> Client.CLIENT_TYPE != Type.RISE);
    public BooleanValue removeSpaces = new BooleanValue("Remove Spaces", this, false, () -> Client.CLIENT_TYPE != Type.RISE);

    public BooleanValue shaders = new BooleanValue("Shaders", this, true);
    private ArrayList<ModuleComponent> allModuleComponents = new ArrayList<>(),
            activeModuleComponents = new ArrayList<>();
    private SubMode lastFrameModulesToShow = (SubMode) modulesToShow.getValue();

    private final StopWatch stopwatch = new StopWatch();
    private final StopWatch updateTags = new StopWatch();

    public Font widthComparator = nunitoNormal;
    public float moduleSpacing = 12, edgeOffset;

    public Interface() {
        createArrayList();
    }

    public void createArrayList() {
        allModuleComponents.clear();
        Client.INSTANCE.getModuleManager().getAll().stream()
                .sorted(Comparator.comparingDouble(module -> -widthComparator.width(Localization.get(module.getDisplayName()))))
                .forEach(module -> allModuleComponents.add(new ModuleComponent(module)));
    }

    public void sortArrayList() {
//        ArrayList<ModuleComponent> components = new ArrayList<>();
//        Client.INSTANCE.getModuleManager().getAll().forEach(module -> components.add(new ModuleComponent(module)));
//
        activeModuleComponents = allModuleComponents.stream()
                .filter(moduleComponent -> moduleComponent.getModule().shouldDisplay(this))
                .sorted(Comparator.comparingDouble(module -> -module.getNameWidth() - module.getTagWidth()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    StopWatch lastUpdate = new StopWatch();

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (lastUpdate.finished(1000)) {
            threadPool.execute(() -> {
                for (final ModuleComponent moduleComponent : allModuleComponents) {
                    moduleComponent.setTranslatedName(Localization.get(moduleComponent.getModule().getDisplayName()));
                }
            });
        }
    };

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        for (final ModuleComponent moduleComponent : allModuleComponents) {
            if (moduleComponent.getModule().isEnabled()) {
                moduleComponent.animationTime = Math.min(moduleComponent.animationTime + stopwatch.getElapsedTime() / 100.0F, 10);
            } else {
                moduleComponent.animationTime = Math.max(moduleComponent.animationTime - stopwatch.getElapsedTime() / 100.0F, 0);
            }
        }

        threadPool.execute(() -> {
            if (updateTags.finished(50)) {
                updateTags.reset();

                for (final ModuleComponent moduleComponent : activeModuleComponents) {
                    if (moduleComponent.animationTime == 0) {
                        continue;
                    }

                    for (final Value<?> value : moduleComponent.getModule().getValues()) {
                        if (value instanceof ModeValue) {
                            final ModeValue modeValue = (ModeValue) value;

                            moduleComponent.setTag(modeValue.getValue().getName());
                            break;
                        }

                        moduleComponent.setTag("");
                    }
                }

                this.sortArrayList();
            }

            final float screenWidth = event.getScaledResolution().getScaledWidth();
            final Vector2f position = new Vector2f(0, 0);
            for (final ModuleComponent moduleComponent : activeModuleComponents) {
                if (moduleComponent.animationTime == 0) {
                    continue;
                }

                moduleComponent.targetPosition = new Vector2d(screenWidth - moduleComponent.getNameWidth() - moduleComponent.getTagWidth(), position.getY());

                if (!moduleComponent.getModule().isEnabled() && moduleComponent.animationTime < 10) {
                    moduleComponent.targetPosition = new Vector2d(screenWidth + moduleComponent.getNameWidth() + moduleComponent.getTagWidth(), position.getY());
                } else {
                    position.setY(position.getY() + moduleSpacing);
                }

                float offsetX = edgeOffset;
                float offsetY = edgeOffset;

                moduleComponent.targetPosition.x -= offsetX;
                moduleComponent.targetPosition.y += offsetY;

                if (Math.abs(moduleComponent.getPosition().getX() - moduleComponent.targetPosition.x) > 0.5 || Math.abs(moduleComponent.getPosition().getY() - moduleComponent.targetPosition.y) > 0.5 || (moduleComponent.animationTime != 0 && moduleComponent.animationTime != 10)) {
                    for (int i = 0; i < stopwatch.getElapsedTime(); ++i) {
                        moduleComponent.position.x = MathUtil.lerp(moduleComponent.position.x, moduleComponent.targetPosition.x, 1.5E-2F);
                        moduleComponent.position.y = MathUtil.lerp(moduleComponent.position.y, moduleComponent.targetPosition.y, 1.5E-2F);
                    }
                } else {
                    moduleComponent.position = moduleComponent.targetPosition;
                }
            }

            stopwatch.reset();
        });
    };
}