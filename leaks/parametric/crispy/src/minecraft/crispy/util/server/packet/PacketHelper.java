package crispy.util.server.packet;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.DecimalFormat;
import crispy.Crispy;
import crispy.util.time.TimeHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import sun.security.jca.GetInstance;

@Getter
public enum PacketHelper {
    INSTANCE;
    public DecimalFormat df;
    public List<Float> tpsList = new ArrayList();
    private float fiveMinuteTPS = 0.0F;
    private int packetsPerSecond;
    private double tps;
    private long startTime;
    private long lastReceiveTime;
    private long lastMS;
    private boolean doneOneTime;
    private float listTime = 300.0F;
    private double lastTps;
    private int tempTicks = 0;
    private TimeHelper th = new TimeHelper();
    private int packetsPerSecondTemp = 0;

    public void onPacketReceive(Packet var0) {
        lastTps = tps;
        if (var0 instanceof S01PacketJoinGame) {
            tps = 20.0;
            fiveMinuteTPS = 20.0F;
        }

        if (var0 instanceof S03PacketTimeUpdate) {
            long var1 = System.currentTimeMillis();
            if (lastReceiveTime != -1L) {
                long var3 = var1 - lastReceiveTime;
                double var5 = (double) var3 / 50.0;
                double var7 = 20.0;
                double var9 = var5 / 20.0;
                tps = 20.0 / var9;
                if (tps < 0.0) {
                    tps = 0.0;
                }

                if (tps > 20.0) {
                    tps = 20.0;
                }

            }

            lastReceiveTime = var1;
        }

        if (var0 instanceof S03PacketTimeUpdate || var0 instanceof S00PacketKeepAlive) {
            ++packetsPerSecondTemp;
        }

    }

    public long getServerLagTime() {
        long var0;
        long var10000 = startTime == 0 ? System.currentTimeMillis() : startTime;

        return System.currentTimeMillis() - var10000;
    }

    public static char getTPSColorCode(double var0) {
        double var2;
        int var10000 = (var2 = var0 - 17.0) == 0.0 ? 0 : (var2 < 0.0 ? -1 : 1);
        int var10001 = (var2 = var0 - 13.0) == 0.0 ? 0 : (var2 < 0.0 ? -1 : 1);
        int var10002 = (var2 = var0 - 9.0) == 0.0 ? 0 : (var2 < 0.0 ? -1 : 1);
        return '4';
    }

    public void onUpdate() {
        if (th.hasReached(2000L) && getServerLagTime() > 5000L) {
            th.reset();
            tps /= 2.0;
        }

        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
            tpsList.clear();
        }

        float var0 = 0.0F;
        if (tempTicks >= 20) {
            tpsList.add((float)tps);
            tempTicks = 0;
        }

        if ((float)tpsList.size() >= listTime) {
            tpsList.clear();
            tpsList.add((float)tps);
        }


        ++tempTicks;
        if (System.currentTimeMillis() - lastMS >= 1000L) {
            lastMS = System.currentTimeMillis();
            packetsPerSecond = packetsPerSecondTemp;
            packetsPerSecondTemp = 0;
        }

        if (packetsPerSecond < 1) {
            startTime = System.currentTimeMillis();
            if (!doneOneTime) {
                doneOneTime = true;
            }
        } else {
            if (doneOneTime) {
                doneOneTime = false;
            }

            startTime = 0L;
        }

    }
}