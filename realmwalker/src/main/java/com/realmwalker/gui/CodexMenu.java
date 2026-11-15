package com.realmwalker.gui;

import com.realmwalker.registry.ModMenuTypes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CodexMenu extends AbstractContainerMenu {
    public CodexMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.CODEX.get(), containerId);
    }

    public CodexMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
