package com.realmwalker.item;

import net.minecraft.world.item.Item;

/**
 * The Sigil of Five: while held in the main hand it grants Speed II, Regeneration I and Jump Boost I.
 * The effect is applied by the common tick handler.
 */
public class SigilOfFiveItem extends Item {
    public SigilOfFiveItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
