package cc.swift.module.impl.render.hud.impl;

import cc.swift.Swift;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.render.HudModule;
import cc.swift.module.impl.render.hud.HudComponent;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.util.ColorUtil;
import cc.swift.util.IMinecraft;
import cc.swift.util.render.animation.Animation;
import cc.swift.util.shader.ShaderProgram;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayListComponent extends HudComponent {
    private final Animation animation = new Animation();
    private ArrayList<Module> modules = new ArrayList<>();
    private final ShaderProgram program = new ShaderProgram("outline.glsl");
    private Framebuffer framebuffer1 = new Framebuffer(1, 1, true);
    private Framebuffer framebuffer2 = new Framebuffer(1, 1, true);

    public ArrayListComponent(float posX, float posY) {
        super(posX, posY);
    }

    @Override
    public void render(HudFont font) {
        ArrayList<Module> oldModules = modules;
        ArrayList<Module> newModules = new ArrayList<>(Swift.INSTANCE.getModuleManager().getModules().values());
        if (!newModules.equals(oldModules) && !newModules.isEmpty()) {
            modules = newModules.stream().filter(Module::isEnabled).collect(Collectors.toCollection(ArrayList::new));
            modules.sort(Comparator.comparingDouble(module -> -font.getStringWidth(module.getName() + (module.getSuffix() != null ? " - " + module.getSuffix() : ""))));
        }

        ScaledResolution sr = new ScaledResolution(mc);

        int opacity = Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListBGOpacity.getValue().intValue();
        int color = Color.CYAN.getRGB();
        double height = font.getFontHeight() + 3;
        double y = Swift.INSTANCE.getModuleManager().getModule(HudModule.class).padding.getValue();

        if (Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListMode.getValue() == HudModule.ArrayListMode.WRAPPED) {
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

            if (framebuffer1.framebufferHeight != mc.displayHeight || framebuffer1.framebufferWidth != mc.displayWidth)
                framebuffer1 = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            if (framebuffer2.framebufferHeight != mc.displayHeight || framebuffer2.framebufferWidth != mc.displayWidth)
                framebuffer2 = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

            framebuffer1.framebufferClear();
            framebuffer2.framebufferClear();
            framebuffer1.setFramebufferColor(0, 0, 0, 0);
            framebuffer2.setFramebufferColor(0, 0, 0, 0);

            framebuffer1.bindFramebuffer(true);

            for (Module module : modules) {
                if (!module.isEnabled()) continue;
                if (module.getCategory() == Module.Category.RENDER) continue;

                switch (Swift.INSTANCE.getModuleManager().getModule(HudModule.class).sideMode.getValue()) {
                    case LEFT:
                        if (Objects.requireNonNull(Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListMode.getValue()) == HudModule.ArrayListMode.WRAPPED) {
                            Gui.drawRect(0, y, font.getStringWidth(module.getName()) + 2, y + font.getFontHeight() + 3, color);
                        }
                        break;
                    case RIGHT:
                        if (Objects.requireNonNull(Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListMode.getValue()) == HudModule.ArrayListMode.WRAPPED) {
                            Gui.drawRect((float) sr.getScaledWidth_double() - font.getStringWidth(module.getName()) - 5, y, sr.getScaledWidth_double() - 2, y + font.getFontHeight() + 3, color);
                        }

                        break;
                }
                y += height;
            }

            y = Swift.INSTANCE.getModuleManager().getModule(HudModule.class).padding.getValue();

            framebuffer1.unbindFramebuffer();
            framebuffer2.bindFramebuffer(true);
            program.start();
            GL20.glUniform1i(GL20.glGetUniformLocation(program.getProgramID(), "u_texture"), 0);
            GL20.glUniform1i(GL20.glGetUniformLocation(program.getProgramID(), "u_depthTexture"), 16);
            GL20.glUniform2f(GL20.glGetUniformLocation(program.getProgramID(), "u_texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
            GL20.glUniform1f(GL20.glGetUniformLocation(program.getProgramID(), "u_radius"), 2);
            GL20.glUniform3f(GL20.glGetUniformLocation(program.getProgramID(), "u_color"), Color.CYAN.getRed() / 255f, Color.CYAN.getBlue() / 255f, Color.CYAN.getGreen() / 255f);
            GL20.glUniform2f(GL20.glGetUniformLocation(program.getProgramID(), "u_direction"), 1, 0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer1.framebufferTexture);
            program.draw();
            mc.getFramebuffer().bindFramebuffer(true);
            GL20.glUniform2f(GL20.glGetUniformLocation(program.getProgramID(), "u_direction"), 0, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer1.framebufferTexture);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer2.framebufferTexture);
            program.draw();
            program.stop();
        }

        for (Module module : modules) {

            if (!module.isEnabled()) continue;
            if (module.getCategory() == Module.Category.RENDER) continue;

            switch (Swift.INSTANCE.getModuleManager().getModule(HudModule.class).sideMode.getValue()) {
                case LEFT:
                    switch (Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListMode.getValue()) {
                        case BASIC:
                            Gui.drawRect(0, y, 1, y + font.getFontHeight() + 3, color);
                            font.drawStringWithShadow(module.getName(), 2, (int) (y + 2), color);
                            break;
                        case WRAPPED:
                            Gui.drawRect(0, y, 1, y + font.getFontHeight() + 3, color);
                            font.drawStringWithShadow(module.getName(), 2, (int) (y + 2), color);
                            break;
                    }
                    break;
                case RIGHT:
                    switch (Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListMode.getValue()) {
                        case BASIC:

                            Gui.drawRect((float) sr.getScaledWidth_double() - 1, y, sr.getScaledWidth_double(), y + font.getFontHeight() + 3, color);
                            font.drawStringWithShadow(module.getName(), (float) (sr.getScaledWidth_double() - font.getStringWidth(module.getName())) - 3, (int) (y + 2), color);
                            break;
                        case WRAPPED:
                            font.drawStringWithShadow(module.getName(), (float) (sr.getScaledWidth_double() - font.getStringWidth(module.getName())) - 3, (int) (y + 2), color);
                            break;
                    }

                    break;
            }
            y += height;
        }
    }
}