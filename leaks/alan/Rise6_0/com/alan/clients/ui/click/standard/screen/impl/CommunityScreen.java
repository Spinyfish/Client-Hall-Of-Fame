package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.component.impl.community.CommunityComponent;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import packet.impl.client.community.ClientCommunityMessageSend;
import util.time.StopWatch;
import util.type.EvictingList;
import lombok.Getter;
import lombok.Setter;
import community.Message;

import java.awt.*;

@Getter
@Setter
public final class CommunityScreen extends Screen implements InstanceAccess {

    private ScrollUtil scrollUtil = new ScrollUtil();
    private TextBox textBox = new TextBox(new Vector2d(0, 0), FontManager.getNunitoBold(20), Color.WHITE, TextAlign.LEFT, "Message", 100);
    private StopWatch timeSinceMessage = new StopWatch(), increaseMaxMessages = new StopWatch();
    private Color color = Color.WHITE;
    private Animation animation = new Animation(Easing.LINEAR, 200);
    private int messages;

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final RiseClickGUI clickGUI = this.getStandardClickGUI();

        double bottomScale = 30;
        double offset = 10;
        double x = clickGUI.position.x + clickGUI.sidebar.sidebarWidth + offset;
        double y = clickGUI.position.y + scrollUtil.getScroll() + offset;
        double startY = y;

        scrollUtil.renderScrollBar(new Vector2d(clickGUI.position.x + clickGUI.scale.x - 5, clickGUI.position.y), clickGUI.scale.y - bottomScale - offset * 2);
        scrollUtil.onRender();

        animation.run(messages <= 0 ? 1 : 0);
        color = ColorUtil.mixColors(Color.RED, clickGUI.sidebarColor, animation.getValue());

        for (Message message : CommunityComponent.messages) {
            boolean draw = y + 30 > clickGUI.position.y && y < clickGUI.position.y + clickGUI.scale.y;
            if (draw) nunitoBoldMedium.drawString(message.username, x, y, getTheme().getFirstColor().getRGB());
            y += 10;

            if (draw) nunitoSmall.drawString(message.message, x, y, Color.WHITE.getRGB());
            y += 20;
        }

        RenderUtil.roundedRectangle(clickGUI.position.x, clickGUI.position.y + clickGUI.scale.y - bottomScale, clickGUI.scale.x, bottomScale, clickGUI.round, color);
        RenderUtil.rectangle(clickGUI.position.x, clickGUI.position.y + clickGUI.scale.y - bottomScale, clickGUI.scale.x, 10, color);

        textBox.setPosition(new Vector2d(x, clickGUI.position.y + clickGUI.scale.y - 18));
        textBox.draw();

        scrollUtil.setMax(-30 * CommunityComponent.messages.size() - bottomScale - offset + clickGUI.scale.y);

        if (!timeSinceMessage.finished(1000) && scrollUtil.scrollingIsAllowed) {
            scrollUtil.setTarget(scrollUtil.getMax());
        }

        if (increaseMaxMessages.finished(1000)) {
            messages = Math.min(5, messages + 1);
            increaseMaxMessages.reset();
        }
    }

    @Override
    public void onKey(char typedChar, int keyCode) {
        textBox.selected = true;
        textBox.key(typedChar, keyCode);

        if (keyCode == 28) {
            if (messages > 0 && !textBox.text.isEmpty()) {
                messages--;
                Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientCommunityMessageSend(getTextBox().text));
                textBox.setText("");
                timeSinceMessage.reset();
            }
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        textBox.click(mouseX, mouseY, mouseButton);
    }

    public boolean automaticSearchSwitching() {
        return false;
    }
}
