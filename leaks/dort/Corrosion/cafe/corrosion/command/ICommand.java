package cafe.corrosion.command;

import net.minecraft.client.Minecraft;

public interface ICommand {
    Minecraft mc = Minecraft.getMinecraft();

    void handle(String[] var1);
}
