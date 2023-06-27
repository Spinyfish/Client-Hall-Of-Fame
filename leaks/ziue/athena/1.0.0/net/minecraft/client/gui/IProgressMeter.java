package net.minecraft.client.gui;

public interface IProgressMeter
{
    public static final String[] lanSearchStates = { "oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO" };
    
    void doneLoading();
}
