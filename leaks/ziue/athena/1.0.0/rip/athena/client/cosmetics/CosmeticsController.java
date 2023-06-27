package rip.athena.client.cosmetics;

import rip.athena.client.cosmetics.cape.*;

public class CosmeticsController
{
    private final CapeManager capeManager;
    
    public CosmeticsController() {
        this.capeManager = new CapeManager();
    }
    
    public CapeManager getCapeManager() {
        return this.capeManager;
    }
}
