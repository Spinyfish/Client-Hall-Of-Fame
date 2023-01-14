package cafe.corrosion;

import net.minecraft.client.Minecraft;

final class Bootstrap$3 extends Thread {
    Bootstrap$3(String x0) {
        super(x0);
    }

    public void run() {
        Minecraft.stopIntegratedServer();
        Corrosion.INSTANCE.end();
    }
}
