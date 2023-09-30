package dev.echo.module.impl.render;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.objects.Dragging;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerList extends Module {

    private final Dragging pos = Echo.INSTANCE.createDrag(this, "playerList", 4, 30);

    public PlayerList() {
        super("PlayerList", Category.RENDER, "Displays a list of players in your world");
    }

    @Link
    public Listener<Render2DEvent> onRender2D = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        List<EntityPlayer> players = mc.theWorld.playerEntities.stream().filter(p -> p != null && !p.isDead).collect(Collectors.toList());

        float height = 35 + (players.size() - 1) * (echoFont16.getHeight() + 8);
        float width = 175;
        float x = pos.getX(), y = pos.getY();
        pos.setWidth(width - 6);
        pos.setHeight(height);

        GlStateManager.color(1, 1, 1, 1);

        Gui.drawRect2(x, y, width - 6, height, new Color(0, 0, 0, 60).getRGB());
        Gui.drawRect2(x, y, width - 6, 1, HUDMod.getClientColors().getFirst().getRGB());
        Gui.drawRect2(x + 4, y + 17, width - 14, 0.5, 0xFFAAAAAA);

        echoBoldFont22.drawString("Player List", x + 4, y + 5, -1, true);

        echoFont18.drawString(String.valueOf(players.size() + 1),
                x + width - echoFont18.getStringWidth(String.valueOf(players.size() + 1)) - 10,
                y + 6, -1, true
        );

        y += 18;

        for (int i = 0; i < players.size(); i++) {
            EntityPlayer player = players.get(i);
            renderPlayer(player, i, x, y);
        }
    };

    private void renderPlayer(EntityPlayer player, int i, float x, float y) {
        float height = echoFont16.getHeight() + 8;
        float offset = i * (height);
        float healthPercent = MathHelper.clamp_float((player.getHealth() + player.getAbsorptionAmount()) / (player.getMaxHealth() + player.getAbsorptionAmount()), 0, 1);
        Color healthColor = healthPercent > .75 ? new Color(66, 246, 123) : healthPercent > .5 ? new Color(228, 255, 105) : healthPercent > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "%";
        echoFont16.drawStringWithShadow("§f§l" + player.getName() + "§r " + healthText, x + 18, y + offset + echoFont16.getMiddleOfBox(height), healthColor);

        float headX = x + 4;
        float headWH = 32;
        float headY = y + offset + height /2f - 6;
        float f = 0.35F;
        RenderUtil.resetColor();
        RenderUtil.scaleStart(headX, headY, f);
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
        Gui.drawTexturedModalRect(headX, headY, (int) headWH, (int) headWH, (int) headWH, (int) headWH);
        RenderUtil.scaleEnd();

        if (player == mc.thePlayer) {
            echoFont18.drawStringWithShadow("*", x + 139 - echoFont18.getStringWidth("*"), y + offset + 6.75F, HUDMod.getClientColors().getFirst().getRGB());
        }
    };
}