package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class MinecraftWrapper extends Wrapper {

    private final String path = "net.minecraft.client.Minecraft", guiScreen = "net.minecraft.client.gui.GuiScreen";
    private static MouseHelperWrapper mouseHelper;
    private static GameSettingsWrapper gameSettings;


    public MinecraftWrapper() {
        super("net.minecraft.client.Minecraft", Juul.INSTANCE.getReflectionHelper().getWithClass(Juul.INSTANCE.getLookupTable().getMethodName("net.minecraft.client.Minecraft", "getMinecraft"), Juul.INSTANCE.getLookupTable().getClassName("net.minecraft.client.Minecraft"), null));
        mouseHelper = new MouseHelperWrapper(mouseHelper());
        gameSettings = new GameSettingsWrapper(gameSettings());
    }


    public Object displayGuiScreen(Object screen) {
        return Juul.INSTANCE.getReflectionHelper().callMethodWithTypes(getDisplayGuiScreenName(), getClassInstance(), new Object[]{screen}, new String[]{getGuiScreenName()});
    }

    public int displayWidth(){ return (int) getField("displayWidth"); }

    public int displayHeight(){ return (int) getField("displayHeight"); }

    public MouseHelperWrapper getMouseHelper() { return mouseHelper; }

    public GameSettingsWrapper getGameSettings() { return gameSettings; }

    public int getRightClickDelayTimer() {return (int) Juul.INSTANCE.getReflectionHelper().getPrivateValue(getRightClickDelayTimerName(), getClassInstance()); }

    public void setRightClickDelayTimer(int delay) { Juul.INSTANCE.getReflectionHelper().setPrivateValue(getRightClickDelayTimerName(), getClassInstance(), delay); }

    public Object playerController() { return getField("playerController"); }

    public Object world() { return getField("theWorld"); }

    public Object player() { return getField("thePlayer"); }

    public Object currentScreen() { return getField("currentScreen"); }

    public Object timer() { return Juul.INSTANCE.getReflectionHelper().getPrivateValue(getTimerName(), getClassInstance()); }

    public Object renderManager() { return Juul.INSTANCE.getReflectionHelper().getPrivateValue(getRenderManagerName(), getClassInstance()); }

    public Object objectMouseOver() { return getField("objectMouseOver");}

    public Object gameSettings() { return getField("gameSettings"); }

    public boolean inGameHasFocus() { return (boolean) getField("inGameHasFocus"); }

    public boolean isUnicode() { return (boolean) getMethodPrimitive("isUnicode"); }

    public Object mouseHelper() { return getField("mouseHelper"); }

    public void clickMouse() { getMethodPrimitive("clickMouse"); }

    public String getClickMouseName() { return getMethodName(path, "clickMouse"); }

    private String getTimerName() { return getFieldName(path, "timer"); }

    private String getDisplayGuiScreenName() { return getMethodName(path, "displayGuiScreen"); }

    private String getRenderManagerName() { return getFieldName(path, "renderManager"); }

    private String getGuiScreenName() { return getLookupTable().getClassName(guiScreen); }

    private String getRightClickDelayTimerName() { return getFieldName(path, "rightClickDelayTimer"); }

}
