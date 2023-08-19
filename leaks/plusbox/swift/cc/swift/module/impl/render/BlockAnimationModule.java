/**
 * @project hakarware
 * @author CodeMan
 * @at 26.07.23, 00:58
 */

package cc.swift.module.impl.render;

import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;

public final class BlockAnimationModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public final DoubleValue xOff = new DoubleValue("X Offset", 0d, -3, 2, 0.05);
    public final DoubleValue yOff = new DoubleValue("Y Offset", 0d, -3, 2, 0.05);
    public final DoubleValue zOff = new DoubleValue("Z Offset", 0d, -3, 0.1, 0.05);

    //public final DoubleValue speed = new DoubleValue("Speed", 4d, 1, 17, 1);

    public BlockAnimationModule() {
        super("BlockAnimation", Category.RENDER);
        this.registerValues(mode, xOff, yOff, zOff);
    }

    public enum Mode {
        OLD, VANILLA, STYLISH, SWIFT, EXHIBITION, TAP
    }
}
