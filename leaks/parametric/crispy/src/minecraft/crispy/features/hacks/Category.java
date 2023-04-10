package crispy.features.hacks;


import crispy.util.animation.Translate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.awt.*;

@RequiredArgsConstructor
@Getter
public enum Category {
    COMBAT("Combat", new Color(198, 22, 34)),
    MOVEMENT("Movement", new Color(208, 13, 166)),
    PLAYER("Player", new Color(103, 39, 179)),
    RENDER("Render", new Color(97, 203, 45)),
    MISC("Misc", new Color(32, 145, 164)),
    CONFIG("Configs", new Color(41, 23, 236));
    private final String name;
    private final Color color;
    @Setter
    private double maxScroll;
    private final Translate currentScroll = new Translate(0,0);

}
