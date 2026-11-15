package com.realmwalker.item;

import com.realmwalker.entity.cogspire.RivetDroneEntity;
import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/**
 * The Wrench: repairs allied constructs (broken Gear Engines, via the engine block) and disables Rivet Drones on hit.
 */
public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties.stacksTo(1).durability(256));
    }

    @Override
    public InteractionResult interactLivingEntityAt(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof RivetDroneEntity && !target.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            target.level().playSound(null, target.blockPosition(), ModSoundEvents.SHRINE_LOOT.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            target.level().addFreshEntity(new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(),
                    new ItemStack(ModItems.SPRING_COIL.get())));
            QuestTracker.add(serverPlayer, "cogspire_drones_disabled", 1);
            target.discard();
            stack.hurtAndBreak(1, serverPlayer, hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Repairs are handled by GearEngineBlock itself, which checks the held item.
        return InteractionResult.PASS;
    }
}
