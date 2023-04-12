package xyz.unnamed.anticheat.model.processor.processors.collision;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.UPacket;
import xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound.PacketInFlying;
import xyz.unnamed.anticheat.model.processor.UProcessor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CollisionProcessor implements UProcessor {

    private PlayerData data;

    private List<CollisionBlock> collisions = new ArrayList<>();

    @Override
    public void update(UPacket packet) {
        if (packet instanceof PacketInFlying) {
            PacketInFlying wrapper = (PacketInFlying) packet;

            if (wrapper.isPos()) return;

            collisions.clear();

            // TODO MAKE IT COMPATIBLE WITH +1.9
            double horizontalExpander = 0.4D;
            double verticalExpander = 1.9D;

            SimpleCollisionBox horizontalBox = new SimpleCollisionBox(
                    wrapper.getX() - horizontalExpander,
                    wrapper.getY(),
                    wrapper.getZ() - horizontalExpander,
                    wrapper.getX() + horizontalExpander,
                    wrapper.getY(),
                    wrapper.getZ() + horizontalExpander
            );

            SimpleCollisionBox verticalBox = new SimpleCollisionBox(
                    wrapper.getX(),
                    wrapper.getY() - 0.1,
                    wrapper.getZ(),
                    wrapper.getX(),
                    wrapper.getY() + verticalExpander,
                    wrapper.getZ()
            );

            SimpleCollisionBox underVerticalBox = verticalBox.copy();
            underVerticalBox.setYMin(verticalBox.getYMin() - 1);

            horizontalBox.getCollidingBlocks(data).forEach(block -> provideCollision(block, CollisionType.HORIZONTAL));
            verticalBox.getCollidingBlocks(data).forEach(block -> provideCollision(block, CollisionType.VERTICAL));
            underVerticalBox.getCollidingBlocks(data).forEach(block -> provideCollision(block, CollisionType.UNDER_WALKING_ON));


        }

    }

    private void provideCollision(Block block, CollisionType type) {
        collisions.add(new CollisionBlock(block, type));
    }

    public boolean isVerticallyCollided(BlockType type) {
        return collisions.stream().anyMatch(collisionBlock -> collisionBlock.getType() == CollisionType.VERTICAL && collisionBlock.get() == type);
    }

    public boolean walkingOn(BlockType type) {
        return collisions.stream().anyMatch(collisionBlock -> (collisionBlock.getType() == CollisionType.VERTICAL
                || collisionBlock.getType() == CollisionType.UNDER_WALKING_ON) && collisionBlock.get() == type);
    }

    public boolean isCollidedHorizontally(BlockType type) {
        return collisions.stream().anyMatch(collisionBlock -> collisionBlock.getType() == CollisionType.HORIZONTAL && collisionBlock.get() == type);
    }

    public boolean isCollided(BlockType type) {
        return walkingOn(type) || isCollidedHorizontally(type);
    }


}
