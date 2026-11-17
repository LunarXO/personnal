package com.lunarxo.gadgetworks.event;

import java.util.List;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.registry.ModEnchantments;
import com.lunarxo.gadgetworks.registry.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Gadgetworks.MODID)
public class ElectricFieldHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(ModItems.COPPER_BOOTS.get())) {
            return;
        }
        int fieldLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELECTRIC_FIELD, boots);
        if (fieldLevel <= 0) {
            return;
        }

        double radius = 3.0D + fieldLevel;
        float damage = 1.5F * fieldLevel;
        List<Mob> targets = level.getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(radius),
                e -> e.isAlive() && !e.isAlliedTo(player));

        boolean zapped = false;
        for (Mob mob : targets) {
            mob.hurt(player.damageSources().playerAttack(player), damage);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                    6, 0.2D, 0.2D, 0.2D, 0.05D);
            zapped = true;
        }

        for (int i = 0; i < 10; i++) {
            double angle = (Math.PI * 2 * i) / 10 + (player.tickCount % 60) / 60.0D;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    player.getX() + Math.cos(angle) * radius * 0.6D,
                    player.getY() + 0.3D,
                    player.getZ() + Math.sin(angle) * radius * 0.6D,
                    1, 0.0D, 0.05D, 0.0D, 0.0D);
        }

        if (zapped) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.PLAYERS, 0.4F, 1.8F);
            boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }
}
