package com.realmwalker.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Lightning Rod: attracts and redirects lightning strikes in Stormspire. Has a POWERED state. */
public class LightningRodBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LightningRodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    public void setPowered(Level level, BlockPos pos, boolean powered) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this) && state.getValue(POWERED) != powered) {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }
}
