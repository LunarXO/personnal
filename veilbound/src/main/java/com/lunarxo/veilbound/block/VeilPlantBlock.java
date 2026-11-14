package com.lunarxo.veilbound.block;

import com.lunarxo.veilbound.registry.ModBlocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VeilPlantBlock extends BushBlock {
    public VeilPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends VeilPlantBlock> codec() {
        return simpleCodec(VeilPlantBlock::new);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.VEILSTONE.get()) || state.is(ModBlocks.COBBLED_VEILSTONE.get())
                || state.is(ModBlocks.VEIL_TURF.get()) || state.is(ModBlocks.VOIDSTONE.get());
    }
}
