package com.lunarxo.veilbound.registry;

import java.util.function.Supplier;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ModTiers implements Tier {
    EMBER(600, 7.0F, 2.0F, 14, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ModItems.EMBER_CORE.get())),
    AETHERITE(1300, 8.5F, 3.0F, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ModItems.AETHERITE_INGOT.get())),
    VOIDSTEEL(2200, 10.0F, 4.5F, 22, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ModItems.VOIDSTONE_INGOT.get()));

    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final TagKey<Block> incorrectBlocksForDrops;
    private final Supplier<Ingredient> repairIngredient;

    ModTiers(int uses, float speed, float attackDamageBonus, int enchantmentValue,
            TagKey<Block> incorrectBlocksForDrops, Supplier<Ingredient> repairIngredient) {
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
