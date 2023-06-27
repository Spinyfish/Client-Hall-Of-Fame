package rip.athena.client.cosmetics.cape;

import java.util.*;
import rip.athena.client.*;

public class CapeManager
{
    private final ArrayList<Cape> capes;
    private Cape selectedCape;
    
    public CapeManager() {
        (this.capes = new ArrayList<Cape>()).add(new Cape("Minecon 2012", "Minecon-2012", "Minecon-2012-display"));
        this.capes.add(new Cape("Minecon 2016", "Minecon-2016", "Minecon-2016-display"));
        this.capes.add(new Cape("ziue's head", "ziue-head", "ziue-head-display"));
        this.capes.add(new Cape("Cat", "cat", "cat-display"));
        this.capes.add(new Cape("None", "None", "None"));
    }
    
    public Cape getCape(final String name) {
        final Cape requestedCape = this.capes.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (requestedCape == null) {
            Athena.INSTANCE.getLog().warn("Tried accessing non-existing cape: " + name);
        }
        return requestedCape;
    }
    
    public ArrayList<Cape> getCapes() {
        return this.capes;
    }
    
    public Cape getSelectedCape() {
        return this.selectedCape;
    }
    
    public void setSelectedCape(final Cape selectedCape) {
        this.selectedCape = selectedCape;
    }
}
