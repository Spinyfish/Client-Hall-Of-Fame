package dev.tenacity.event;

import dev.tenacity.event.impl.game.*;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.network.PacketReadEvent;
import dev.tenacity.event.impl.network.message.ChatReceivedEvent;
import dev.tenacity.event.impl.network.message.ChatSentEvent;
import dev.tenacity.event.impl.player.input.*;
import dev.tenacity.event.impl.network.PacketEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.*;
import dev.tenacity.event.impl.player.movement.*;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.event.impl.render.*;
import dev.tenacity.event.impl.render.elements.RenderHotbarEvent;
import dev.tenacity.event.impl.render.elements.RenderNametagEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author cedo
 * @since 04/18/2022
 */
public abstract class ListenerAdapter implements EventListener {

    // Game
    public void onGameCloseEvent(GameCloseEvent event) {}

    public void onLegitClickEvent(LegitClickEvent event) {}
    public void onKeyPressEvent(KeyInputEvent event) {}
    public void onRenderTickEvent(RenderTickEvent event) {}
    public void onTickEvent(TickEvent event) {}
    public void onWorldEvent(WorldEvent event) {}
    public void onTeleportEvent(TeleportEvent event) {}


    // Network
    public void onPacketReceiveEvent(PacketReceiveEvent event) {}
    public void onPacketSendEvent(PacketSendEvent event) {}
    public void onPacketEvent(PacketEvent event) {}

    public void onPacketReadEvent(PacketReadEvent event) {}

    // Player
    public void onAttackEvent(AttackEvent event) {}
    public void onBlockEvent(BlockEvent event) {}
    public void onBoundingBoxEvent(BoundingBoxEvent event) {}
    public void onChatReceivedEvent(ChatReceivedEvent event) {}
    public void onClickEvent(ClickEvent event) {}
    public void onClickEventRight(ClickEventRight event) {}
    public void onJumpEvent(JumpEvent event) {}
    public void onJumpFixEvent(JumpEvent event) {}
    public void onKeepSprintEvent(LoseSprintEvent event) {}
    public void onMotionEvent(MotionEvent event) {}
    public void onMoveEvent(MoveEvent event) {}
    public void onStrafeEvent(StrafeEvent event) {}
    public void onMoveInputEvent(MoveInputEvent event) {}
    public void onPlayerSendMessageEvent(ChatSentEvent event) {}
    public void onPushOutOfBlockEvent(PushOutOfBlockEvent event) {}
    public void onSafeWalkEvent(SafeWalkEvent event) {}
    public void onSlowDownEvent(SlowdownEvent event) {}
    public void onStepConfirmEvent(StepConfirmEvent event) {}
    public void onUpdateEvent(UpdateEvent event) {}

    // Render
    public void onRenderNametagEvent(RenderNametagEvent event) {}
    public void onRenderHotbarEvent(RenderHotbarEvent event) {}

    public void onHurtCamEvent(HurtCamEvent event) {}
    public void onPreRenderEvent(PreRenderEvent event) {}
    public void onRender2DEvent(Render2DEvent event) {}
    public void onRender3DEvent(Render3DEvent event) {}
    public void onRenderChestEvent(RenderChestEvent event) {}
    public void onRendererLivingEntityEvent(RendererLivingEntityEvent event) {}
    public void onRenderModelEvent(RenderModelEvent event) {}
    public void onShaderEvent(ShaderEvent event) {}

    private final Map<Class<? extends Event>, Consumer<Event>> methods = new HashMap<>();

    private boolean registered = false;

    @Override
    public void onEvent(Event event) {
        if (!registered) {
            start();
            registered = true;
        }
        methods.get(event.getClass()).accept(event);
    }

    private <T> void registerEvent(Class<T> clazz, Consumer<T> consumer) {
        methods.put((Class<? extends Event>) clazz, (Consumer<Event>) consumer);
    }

    private void start() {
        // Game
        registerEvent(GameCloseEvent.class, this::onGameCloseEvent);
        registerEvent(KeyInputEvent.class, this::onKeyPressEvent);
        registerEvent(RenderTickEvent.class, this::onRenderTickEvent);
        registerEvent(TickEvent.class, this::onTickEvent);
        registerEvent(TeleportEvent.class, this::onTeleportEvent);

        registerEvent(WorldEvent.Load.class, this::onWorldEvent);
        registerEvent(WorldEvent.Unload.class, this::onWorldEvent);
        registerEvent(WorldEvent.class, this::onWorldEvent);
        registerEvent(LegitClickEvent.class, this::onLegitClickEvent);

        registerEvent(PacketReceiveEvent.class, this::onPacketReceiveEvent);
        registerEvent(PacketReadEvent.class, this::onPacketReadEvent);
        registerEvent(PacketSendEvent.class, this::onPacketSendEvent);
        registerEvent(PacketEvent.class, this::onPacketEvent);

        // Player
        registerEvent(AttackEvent.class, this::onAttackEvent);
        registerEvent(BlockEvent.class, this::onBlockEvent);
        registerEvent(BoundingBoxEvent.class, this::onBoundingBoxEvent);
        registerEvent(ChatReceivedEvent.class, this::onChatReceivedEvent);
        registerEvent(ClickEvent.class, this::onClickEvent);
        registerEvent(ClickEventRight.class, this::onClickEventRight);
        registerEvent(JumpEvent.class, this::onJumpEvent);
        registerEvent(JumpEvent.class, this::onJumpFixEvent);
        registerEvent(LoseSprintEvent.class, this::onKeepSprintEvent);
        registerEvent(MotionEvent.class, this::onMotionEvent);
        registerEvent(MoveEvent.class, this::onMoveEvent);
        registerEvent(StrafeEvent.class, this::onStrafeEvent);
        registerEvent(MoveInputEvent.class, this::onMoveInputEvent);
        registerEvent(ChatSentEvent.class, this::onPlayerSendMessageEvent);
        registerEvent(PushOutOfBlockEvent.class, this::onPushOutOfBlockEvent);
        registerEvent(SafeWalkEvent.class, this::onSafeWalkEvent);
        registerEvent(SlowdownEvent.class, this::onSlowDownEvent);
        registerEvent(StepConfirmEvent.class, this::onStepConfirmEvent);
        registerEvent(UpdateEvent.class, this::onUpdateEvent);

        // Render
        registerEvent(RenderNametagEvent.class, this::onRenderNametagEvent);
        registerEvent(RenderHotbarEvent.class, this::onRenderHotbarEvent);
        registerEvent(HurtCamEvent.class, this::onHurtCamEvent);
        registerEvent(PreRenderEvent.class, this::onPreRenderEvent);
        registerEvent(Render2DEvent.class, this::onRender2DEvent);
        registerEvent(Render3DEvent.class, this::onRender3DEvent);
        registerEvent(RenderChestEvent.class, this::onRenderChestEvent);
        registerEvent(RendererLivingEntityEvent.class, this::onRendererLivingEntityEvent);
        registerEvent(RenderModelEvent.class, this::onRenderModelEvent);
        registerEvent(ShaderEvent.class, this::onShaderEvent);
    }

}
