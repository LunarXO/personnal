package com.realmwalker.item;

import com.realmwalker.portal.WaystoneFrame;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * A dimension-specific Spark. Used on a Waystone Brick frame to light a portal to the spark's dimension.
 */
public class SparkItem extends Item {
    private final ResourceKey<Level> target;

    public SparkItem(Properties properties, ResourceKey<Level> target) {
        super(properties.stacksTo(1));
        this.target = target;
    }

    public ResourceKey<Level> getTarget() {
        return this.target;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (WaystoneFrame.light(level, context.getClickedPos(), player, this.target)) {
            level.playSound(null, context.getClickedPos(), ModSoundEvents.SPARK_IGNITE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (player != null && !level.isClientSide && !WaystoneFrame.isFrameBlock(level.getBlockState(context.getClickedPos()))) {
            player.displayClientMessage(Component.translatable("message.realmwalker.spark_needs_frame"), true);
        }
        return InteractionResult.PASS;
    }
}
