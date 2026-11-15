package com.realmwalker.trigger;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import com.realmwalker.Realmwalker;

public class GenericTrigger extends SimpleCriterionTrigger<GenericTrigger.Instance> {
    public static final ResourceLocation ID = Realmwalker.id("generic");

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, String event, int achieved) {
        this.trigger(player, instance -> instance.event().equals(event) && achieved >= instance.minCount());
    }

    public static class Instance extends SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(SimpleInstance::player),
                Codec.STRING.fieldOf("event").forGetter(Instance::event),
                Codec.INT.optionalFieldOf("min_count", 1).forGetter(Instance::minCount)
        ).apply(builder, Instance::new));

        private final String event;
        private final int minCount;

        public Instance(Optional<ContextAwarePredicate> player, String event, int minCount) {
            super(player);
            this.event = event;
            this.minCount = minCount;
        }

        public String event() {
            return this.event;
        }

        public int minCount() {
            return this.minCount;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }
    }
}
