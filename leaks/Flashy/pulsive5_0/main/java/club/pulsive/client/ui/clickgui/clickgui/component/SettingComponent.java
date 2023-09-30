package club.pulsive.client.ui.clickgui.clickgui.component;


import club.pulsive.impl.property.Property;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class SettingComponent<Type extends Property> extends Component {

    protected Type setting;

    public SettingComponent(Type setting, float x, float y, float width, float height) {
        this(setting, x, y, width, height, true);
    }

    public SettingComponent(Type setting, float x, float y, float width, float height, boolean visible) {
        super(x, y, width, height, visible);
        this.setting = setting;
    }
}
