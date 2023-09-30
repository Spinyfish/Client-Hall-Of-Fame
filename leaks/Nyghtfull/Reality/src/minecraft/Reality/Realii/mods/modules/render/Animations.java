package Reality.Realii.mods.modules.render;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Animations extends Module {

    public static Mode mode = new Mode("Mode", "Mode", new String[]{"NONE", "1.7", "Swang", "Swank","rotate","Drop","Mercy","exchibobo","Pula","Clean","idk","Exchibition2","Cleaner","Exchibition"}, "1.7");
    public static Numbers<Number>  scale = new Numbers<>("Scale", 0.15f, 0.8f, 15.0f, 8.0f);
    public static Numbers<Number>  x = new Numbers<>("Scale", 0.15f, 0.8f, 15f, 8f);
    public static Numbers<Number>  y = new Numbers<>("Scale", 0.15f, 0.8f, 15f, 8f);
    public static Numbers<Number>  z = new Numbers<>("Scale", 0.15f, 0.8f, 15f, 8f);
    
    public static Numbers<Number> speed = new Numbers<>("Speed", 1, 1, 30, 1);
    private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);

    public Animations() {
        super("Animations", ModuleType.Render);
        addValues(mode, speed, this.sufix, scale);
      
        
        
    }
    
    
    @EventHandler
    public void onUpdate(EventPreUpdate e) {
    this.setSuffix(mode.getModeAsString());
}
}
