# Gadgetworks

A NeoForge 1.21.1 mod adding a small arsenal of functional gadgets.

## Items

- **Grappling Hook** - right-click a block within 32 blocks to launch yourself toward it (1s cooldown).
- **Electric Sword** - hits arc lightning to up to 3 nearby mobs for bonus damage.
- **Magnetic Double Sword** - double-sided blade. Sneak + right-click to flip it:
  - **North Pole**: pulls nearby mobs toward you on hit.
  - **South Pole**: blasts nearby mobs away on hit.
- **Ground Breaking Spear** - +2 block reach; right-click on the ground to slam a shockwave that damages and launches all mobs around you.
- **Redstone Staff** - sneak + right-click to cycle modes, right-click to use:
  - **Summon Golem**: spawns a Mini Redstone Golem that follows and fights for you (max 3).
  - **Recall Golems**: despawns all your golems.
  - **Redstone Zap**: fires a redstone spark projectile.
  - **Static Pulse**: AoE damage + short paralysis around you.
- **Copper Boots** - new copper armor piece, and the only item that accepts the new enchantment.

## Enchantment

- **Electric Boots** (levels I-III) - only fits Copper Boots. Surrounds the wearer with an electric field that shocks hostile mobs every second within (3 + level) blocks.

## Entities

- **Mini Redstone Golem** - small construct summoned by the Redstone Staff; follows its owner, defends them, attacks monsters. Also has a spawn egg.

## Recipes

All items are craftable (see `src/main/resources/data/gadgetworks/recipe/`).

## Building

```
./gradlew build
```
