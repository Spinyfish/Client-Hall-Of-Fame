package rip.athena.client.gui.clickgui;

public interface IPage
{
    void onInit();
    
    void onRender();
    
    void onLoad();
    
    void onUnload();
    
    void onOpen();
    
    void onClose();
}
