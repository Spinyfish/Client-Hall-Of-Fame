package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.render.Draggable;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.shaders.Bloom;
import club.pulsive.impl.util.render.shaders.Blur;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.play.server.S02PacketChat;

import java.awt.*;

@ModuleInfo(name = "Staff Information", description = "Just Da Staff Information", category = Category.VISUALS)
public class StaffBanInfo extends Module {

    public Draggable draggable = Pulsive.INSTANCE.getDraggablesManager().createNewDraggable(this, "staffbaninfo", 30, 100);
    private Framebuffer shadowFramebuffer = new Framebuffer(1, 1, false);
    private TimerUtil timer = new TimerUtil();
    public int fiveminbans,tenminbans,thirtyminbans;
    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        draggable.setWidth(140);
        draggable.setHeight(40);
        //  ShaderEvent shaderEvent = new ShaderEvent(ShaderEvent.ShaderType.BLUR);
//       if(shaderEvent.isBlur()) {
//            StencilUtil.initStencilToWrite();
//            shaderEvent.setShaderType(ShaderEvent.ShaderType.BLUR);
//            RoundedUtil.drawRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(),12, new Color(12,12,12,255).getRGB());
//            Pulsive.INSTANCE.getEventBus().call(shaderEvent);
//            StencilUtil.readStencilBuffer(1);
//            Blur.renderBlur(10);
//            StencilUtil.uninitStencilBuffer();
//        }
        RoundedUtil.drawGradientRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) 0.1).getRGB(), RenderUtil.applyOpacity(new Color(HUD.getColor()).darker(), (float) 0.1f).getRGB());

        float ez = 2;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        int posX = (int) draggable.getX() * 2, posY = (int) draggable.getY() * 2,
                width = (int) draggable.getWidth() * 2, height = (int) draggable.getHeight() * 2;
        RoundedUtil.drawRoundedRect(draggable.getX(),draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8,  new Color(0,0,0, 100).getRGB());
        Fonts.icons30.drawCenteredString("b", (posX + width / 6) / ez - 3, (posY + height / 2) / ez - 5, new Color(230,230,230, 200).getRGB());
        Fonts.icons15.drawCenteredString("L", (posX + width) / ez - 10 , (posY + height - 16) / ez + 3 - 5, new Color(230,230,230, 200).getRGB());
        Fonts.icons15.drawCenteredString("K", (posX + width - 20) / ez - 10 , (posY + height - 16) / ez + 3 - 5, new Color(230,230,230, 200).getRGB());
        if(ClientSettings.uiOutlines.getValue()) {
            RoundedUtil.drawRoundedOutline(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(),8,3, new Color((HUD.getColor())).getRGB());
        }
        RenderUtil.color(-1);

        Fonts.googleSmall.drawString("The Last 5m: " + fiveminbans, draggable.getX() + 4  + width / 8, draggable.getY() + 2 + 12 - 8 , -1);
        Fonts.googleSmall.drawString("The Last 10m: " + tenminbans, draggable.getX() + 4  + width / 8, draggable.getY() + 2 + 12 * 2 - 8, -1);
        Fonts.googleSmall.drawString("The Last 30m: " + thirtyminbans, draggable.getX() + 4  + width / 8, draggable.getY() + 2 + 12 * 3 - 8, -1);
        //Gui.drawRect(1,1,100,100,-1);
    };

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case RECEIVING:

                break;
        }
    };

    @EventHandler
    private final Listener<ShaderEvent> shaderEvent = event -> {
        RoundedUtil.drawRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(),8, new Color(12,12,12,255).getRGB());
    };
}