package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.fbi.FBI;
import crispy.util.fbi.data.Data;
import crispy.util.fbi.data.DataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@HackInfo(name = "HackerDetector", category = Category.MISC)
public class HackerDetector extends Hack {
    ScheduledExecutorService checkThread = Executors.newSingleThreadScheduledExecutor();
    public ModeValue mode = new ModeValue("Alert Mode", "Notification", "Notification", "Text");
    public BooleanValue critToggle = new BooleanValue("Crit Toggle", false);
    public BooleanValue cpsIncrease = new BooleanValue("Cps Increase", false);
    public BooleanValue dodge = new BooleanValue("Dodge Increase", false);

    public HackerDetector() {

    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            for (Object object : Minecraft.theWorld.loadedEntityList) {
                if (object instanceof EntityPlayer) {
                    EntityPlayer entity = (EntityPlayer) object;
                    Data data = DataManager.INSTANCE.getData(entity);
                    checkThread.execute(() -> data.getChecks().forEach(check -> check.run(data)));
                }
            }
        }
    }
}
