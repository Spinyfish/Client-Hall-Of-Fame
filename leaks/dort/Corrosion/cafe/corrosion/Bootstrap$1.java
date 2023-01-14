package cafe.corrosion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.stats.IStatStringFormat;

final class Bootstrap$1 implements IStatStringFormat {
    // $FF: synthetic field
    final Minecraft val$minecraft;

    Bootstrap$1(Minecraft var1) {
        this.val$minecraft = var1;
    }

    public String formatString(String p_74535_1_) {
        try {
            return String.format(p_74535_1_, GameSettings.getKeyDisplayString(this.val$minecraft.gameSettings.keyBindInventory.getKeyCode()));
        } catch (Exception var3) {
            return "Error: " + var3.getLocalizedMessage();
        }
    }
}
