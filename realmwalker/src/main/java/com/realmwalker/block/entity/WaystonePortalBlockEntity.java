package com.realmwalker.block.entity;

import com.realmwalker.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WaystonePortalBlockEntity extends BlockEntity {
    private String target = "minecraft:overworld";
    private String returnDim = "minecraft:overworld";

    public WaystonePortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WAYSTONE_PORTAL.get(), pos, state);
    }

    public String getTarget() {
        return this.target;
    }

    public String getReturnDim() {
        return this.returnDim;
    }

    public void setTarget(String target) {
        this.target = target;
        this.setChanged();
    }

    public void setReturnDim(String returnDim) {
        this.returnDim = returnDim;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("target", this.target);
        tag.putString("return_dim", this.returnDim);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("target")) {
            this.target = tag.getString("target");
        }
        if (tag.contains("return_dim")) {
            this.returnDim = tag.getString("return_dim");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }
}
