package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.event.game.MouseOverEvent;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjglx.input.Mouse;

public class Reach extends Module {

    public final NumberSetting range = new NumberSetting("Range", 4, 3, 6, 0.1);
    private final NumberSetting bufferDecrease = new NumberSetting("Buffer Decrease", 1, 0.1, 10, 0.1);
    private final NumberSetting maxBuffer = new NumberSetting("Max Buffer", 5, 1, 200, 1);
    private final BooleanSetting bufferAbuse = new BooleanSetting("Buffer Abuse", false);

    private int lastId, attackTicks;
    private Vector2f rots;
    private double combo;
    private int exempt;

    public Reach(){
        super("Reach", Category.GHOST);
        this.bufferDecrease.addParent(bufferAbuse, ParentAttribute.BOOLEAN_CONDITION);
        this.maxBuffer.addParent(bufferAbuse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(range, bufferDecrease, maxBuffer, bufferAbuse);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()) {
            this.attackTicks++;
            exempt--;
            this.rots = new Vector2f(event.getYaw(), event.getPitch());
        }
    };

    @Callback
    final EventCallback<MouseOverEvent> mouseOverEventEventCallback = event -> {
        if (Mouse.isButtonDown(1)) {
            exempt = 1;
        }

        if (exempt > 0)
            return;

        event.setRange(this.range.getValue());
    };

    @Callback
    final EventCallback<PlayerAttackEvent> playerAttackEventEventCallback = event -> {
        final Entity entity = event.getTarget();

        if (this.bufferAbuse.isEnabled()) {
            if (RotationUtils.rayCast(rots, 3.0D).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                if ((this.attackTicks > 9 || entity.getEntityId() != this.lastId) && this.combo < this.maxBuffer.getValue().intValue()) {
                    this.combo++;
                } else {
                    event.setCancelled(true);
                }
            } else {
                this.combo = Math.max(0, this.combo - this.bufferDecrease.getValue());
            }
        } else {
            this.combo = 0;
        }

        this.lastId = entity.getEntityId();
        this.attackTicks = 0;
    };
}
