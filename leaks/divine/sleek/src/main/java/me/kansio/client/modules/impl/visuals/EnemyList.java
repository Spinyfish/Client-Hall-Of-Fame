package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.render.ColorUtils;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

@ModuleData(
        name = "Enemy List",
        category = ModuleCategory.VISUALS,
        description = "Shows all enemies in ur render distance on ur screen"
)
public class EnemyList extends Module {

    private NumberValue<Integer> xpos = new NumberValue<>("X-Pos", this, 5, 0, 1000, 1);
    private NumberValue<Integer> ypos = new NumberValue<>("Y-Pos", this, 200, 0, 1000, 1);

    private BooleanValue hideIfEmpty = new BooleanValue("Hide If Empty", this, true);

    @Subscribe
    public void onRender(RenderOverlayEvent event) {
        List<String> enemies = Client.getInstance().getTargetManager().getTarget();
        HashMap<String, Double> enemiesLoaded = new HashMap<>();

        //get all entities
        for (Entity ent : mc.theWorld.loadedEntityList) {
            if (!(ent instanceof EntityPlayer)) continue;

            EntityPlayer player = (EntityPlayer) ent;

            for (String enemy : enemies) {
                if (player.getName().equalsIgnoreCase(enemy)) {
                    enemiesLoaded.put(enemy, (double) mc.thePlayer.getDistanceToEntity(ent));
                }
            }
        }

        //return if no entities and hide if empty is toggled
        if (hideIfEmpty.getValue() && enemiesLoaded.isEmpty()) {
            return;
        }

        RenderUtils.drawRect(xpos.getValue(), ypos.getValue(), 130, 1, ColorUtils.getColorFromHud(1).getRGB());
        RenderUtils.drawRect(xpos.getValue(), ypos.getValue() + 1, 130, 15, new Color(0, 0, 0, 100).getRGB());


        int y = ypos.getValue() + 10;

        HUD hud = Client.getInstance().getModuleManager().getModuleByClass(HUD.class);

        if (!hud.font.getValue())
            mc.fontRendererObj.drawStringWithShadow("Enemies | " + enemiesLoaded.keySet().size(), xpos.getValue() + 5, ypos.getValue() + 5, -1);
        else
            Fonts.SFRegular.drawStringWithShadow("Enemies | " + enemiesLoaded.keySet().size(), xpos.getValue() + 5, ypos.getValue() + 5, -1);

        for (String enemy : enemiesLoaded.keySet()) {
            double dist = enemiesLoaded.get(enemy);

            RenderUtils.drawRect(xpos.getValue(), 6 + y, 130, 13, new Color(0, 0, 0, 100).getRGB());

            if (!hud.font.getValue())
                mc.fontRendererObj.drawStringWithShadow("  §7» §c" + Math.round(dist) + "§7: §f" + enemy, xpos.getValue(), y + 6, -1);
            else
                Fonts.SFRegular.drawStringWithShadow("  §7» §c" + Math.round(dist) + "§7: §f" + enemy, xpos.getValue(), y + 6, -1);

            y = y + 13;
        }
    }
}
