package club.marsh.bloom.impl.ui.hud.impl;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.ui.hud.Component;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import static club.marsh.bloom.impl.mods.render.Hud.rgb;

public class BalanceComponent extends Component {
    public BalanceComponent() {
        super("Balance", ScaledResolution.getScaledWidth()/2,0,false);
    }
    public static long balance = 0;
    public static long lastPacket;
    @Override
    public void render() {
        if (balance < -75000)
            balance = -75000;
        setWidth(getMc().fontRendererObj.getStringWidth("balance: " + -balance));
        setHeight(getMc().fontRendererObj.FONT_HEIGHT);
        //System.out.println("LOL");
        renderText("balance: " + -balance, getX(), getY(), 1);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (lastPacket == 0) lastPacket = System.currentTimeMillis();
            long delay = System.currentTimeMillis() - lastPacket;
            balance += event.isCancelled() ? -delay : 50 - delay;
            lastPacket = System.currentTimeMillis();
        } else if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            balance -= 50;
        } else if (event.getPacket() instanceof S00PacketDisconnect || event.getPacket() instanceof S01PacketJoinGame || event.getPacket() instanceof C00PacketLoginStart) {
            balance = lastPacket = 0;
        }
    }

    public int renderText(String text, double x,double y,int thing) {
        int ind = 0;
        char[] t = text.toCharArray();
        for (int i = 0; i <= text.length()-1; ++i) {
            getMc().fontRendererObj.drawString(String.valueOf(t[i]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            ind += getMc().fontRendererObj.getCharWidth(t[i]);
            thing += 1;
        }
        return thing;
    }

    @Override
    protected boolean isHolding(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseY >= getY() && mouseX < getX()+getWidth() && mouseY < getY()+getHeight();
    }

}
