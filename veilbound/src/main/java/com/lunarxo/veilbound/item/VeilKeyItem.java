package com.lunarxo.veilbound.item;

import com.lunarxo.veilbound.registry.ModBlocks;
import com.lunarxo.veilbound.registry.ModParticles;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class VeilKeyItem extends Item {
    private final Supplier<Block> frame;
    private final Supplier<Block> portal;

    public VeilKeyItem(Properties properties, Supplier<Block> frame, Supplier<Block> portal) {
        super(properties.stacksTo(1));
        this.frame = frame;
        this.portal = portal;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(this.frame.get())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, this.portal.get().defaultBlockState());
            level.playSound(null, pos, ModSoundEvents.PORTAL_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((ParticleOptions) ModParticles.SHARD_SPARKLE.get(),
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 40, 0.4D, 0.4D, 0.4D, 0.05D);
            }
            Player player = context.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
