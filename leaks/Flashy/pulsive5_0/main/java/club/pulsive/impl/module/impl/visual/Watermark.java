package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.network.BalanceUtil;
import club.pulsive.impl.util.render.Draggable;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ModuleInfo(name = "Watermark", description = "Watermark Settings", category = Category.CLIENT)
public class Watermark extends Module {

    public Draggable draggable = Pulsive.INSTANCE.getDraggablesManager().createNewDraggable(this, "watermark", 4, 4);
    private final Property<Boolean> outlined = new Property<>("Outline", true);
    private final MultiSelectEnumProperty<THINGS> renderStrings = new MultiSelectEnumProperty<>("Strings", Lists.newArrayList(THINGS.TIME), THINGS.values());
    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        Gui.drawRect(0, 0, 0, 0, -1);
        //Time
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String dateString = dateFormat.format(new Date()).toString();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Pulsive.INSTANCE.getClientInfo().getClientName());
        stringBuilder.append(" | " + Pulsive.INSTANCE.getClientInfo().getClientVersion());
        if(renderStrings.isSelected(THINGS.TIME)) {
            stringBuilder.append(" | " + dateString);
        }
        if(renderStrings.isSelected(THINGS.USERNAME)) {
            stringBuilder.append(" | " + mc.thePlayer.getName());
        }
        if(renderStrings.isSelected(THINGS.SERVER_IP) && !mc.isSingleplayer()) {
            stringBuilder.append(" | " + mc.getCurrentServerData().serverIP);
        }
        if(renderStrings.isSelected(THINGS.BALANCE)){
            stringBuilder.append(" | " + BalanceUtil.INSTANCE.getBalance());
        }

        String name = stringBuilder.toString();
        int stringWidth = Fonts.fontforflashy.getStringWidth(name);
        draggable.setHeight(10 + 1);
        draggable.setWidth(stringWidth + 4);
        RoundedUtil.drawRoundedRect(draggable.getX(),draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8,  new Color(0,0,0, 100).getRGB());

        RoundedUtil.drawGradientRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) 0.1).getRGB(), RenderUtil.applyOpacity(new Color(HUD.getColor()).darker(), (float) 0.1f).getRGB());

        if(ClientSettings.uiOutlines.getValue()) {
            RoundedUtil.drawRoundedOutline(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(),6,3, new Color((HUD.getColor())).getRGB());
        }
        Fonts.fontforflashy.drawStringWithShadow(name, draggable.getX() + 2, draggable.getY() + 2, -1);
    };
    @EventHandler
    private final Listener<ShaderEvent> shaderEventListener = event -> {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String dateString = dateFormat.format(new Date()).toString();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Pulsive.INSTANCE.getClientInfo().getClientName());
        stringBuilder.append(" | " + Pulsive.INSTANCE.getClientInfo().getClientVersion());
        if(renderStrings.isSelected(THINGS.TIME)) {
            stringBuilder.append(" | " + dateString);
        }
        if(renderStrings.isSelected(THINGS.USERNAME)) {
            stringBuilder.append(" | " + mc.thePlayer.getName());
        }
        if(renderStrings.isSelected(THINGS.SERVER_IP) && !mc.isSingleplayer()) {
            stringBuilder.append(" | " + mc.getCurrentServerData().serverIP);
        }
        if(renderStrings.isSelected(THINGS.BALANCE)){
            stringBuilder.append(" | " + BalanceUtil.INSTANCE.getBalance());
        }
        String name = stringBuilder.toString();
        int stringWidth = Fonts.fontforflashy.getStringWidth(name);

        if(outlined.getValue()) {
            RoundedUtil.drawGradientRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) 0.1).getRGB(), RenderUtil.applyOpacity(new Color(HUD.getColor()).darker(), (float) 0.1f).getRGB());

            if(ClientSettings.uiOutlines.getValue()) {
                RoundedUtil.drawRoundedOutline(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(),6,3, new Color((HUD.getColor())).getRGB());
            }
        }
    };

    @AllArgsConstructor
    private enum THINGS {
        TIME("Time"),
        USERNAME("Username"),
        SERVER_IP("Server IP"),
        BALANCE("Balance");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }
}