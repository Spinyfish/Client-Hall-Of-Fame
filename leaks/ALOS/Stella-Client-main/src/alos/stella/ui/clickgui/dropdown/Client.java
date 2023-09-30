package alos.stella.ui.clickgui.dropdown;

public class Client {
    private static Client INSTANCE;
    public DropdownGUI dropDownGUI;
    public DropdownGUI getDropDownGUI() {
        return dropDownGUI;
    }
    public static Client getInstance() {

        try {
            if (INSTANCE == null) INSTANCE = new Client();
            return INSTANCE;
        } catch (Throwable t) {
           // ClientUtils.logError("Dropdown [e]:", t);
            throw t;
        }
    }
}
