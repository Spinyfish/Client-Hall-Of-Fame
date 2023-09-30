package dev.africa.pandaware.switcher.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import dev.africa.pandaware.switcher.ViaMCP;
import dev.africa.pandaware.switcher.protocols.ProtocolCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuiProtocolSlider extends GuiButton {
    private ProtocolVersion protocolVersion;
    private float sliderValue;
    private boolean dragging;

    private final List<ProtocolCollection> versions = Arrays.asList(ProtocolCollection.values());

    private final float valueMin = 0;
    private final float valueMax = versions.size() - 1;

    public GuiProtocolSlider(int id, int x, int y, int width, int height, ProtocolVersion version) {
        super(id, x, y, width, height, "");
        this.versions.sort(Comparator.comparingInt(v -> v.getVersion().getVersion()));

        this.sliderValue = normalizeValue(versions.indexOf(version));
        this.protocolVersion = version;
        this.displayString = "Via -> §3" + version.getName();
    }

    public void update(int x, int y, String text) {
        this.xPosition = x;
        this.yPosition = y;
        this.displayString = text;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                int f = (int) denormalizeValue(this.sliderValue);
                ProtocolVersion version = versions.get(f).getVersion();
                ViaMCP.getInstance().setVersion(version.getVersion());
                this.protocolVersion = version;
                this.sliderValue = normalizeValue(f);
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            int f = (int) denormalizeValue(this.sliderValue);
            ProtocolVersion version = versions.get(f).getVersion();
            ViaMCP.getInstance().setVersion(version.getVersion());
            this.protocolVersion = version;
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    public float normalizeValue(float p_148266_1_) {
        return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin)
                / (this.valueMax - this.valueMin), 0.0F, 1.0F);
    }

    public float denormalizeValue(float p_148262_1_) {
        return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin)
                * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
    }

    public float snapToStepClamp(float p_148268_1_) {
        return MathHelper.clamp_float(this.snapToStep(p_148268_1_), this.valueMin, this.valueMax);
    }

    protected float snapToStep(float p_148264_1_) {
        return Math.round(p_148264_1_);
    }
}