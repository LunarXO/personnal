# Gadgetworks

A NeoForge 1.21.1 mod adding a small arsenal of functional gadgets.

## Items

- **Grappling Hook** - aim at a block within 32 blocks and hold right-click; the hook reels you in until you reach it (crit trail particles, fall damage reset, small cooldown after arrival).
- **Electric Sword**
  - Passive: hits arc lightning to up to 3 nearby mobs for bonus damage.
  - Active: right-click to **Arc Strike** the mob you are looking at (16 blocks) - heavy damage plus a chain to nearby mobs (3s cooldown).
- **Magnetic Double Sword** - double-sided blade. Sneak + right-click to flip it:
  - **North Pole**: pulls nearby mobs toward you (on hit and on right-click).
  - **South Pole**: blasts nearby mobs away (on hit and on right-click).
  - Active: right-click to magnetize every mob within 8 blocks.
- **Ground Breaking Hammer** - right-click on the ground to slam a shockwave that damages and launches all mobs around you.
- **Frostbrand Axe** (new weapon)
  - Passive: hits chill enemies (Slowness II + Weakness).
  - Active: right-click to unleash a **Frost Nova** - AoE damage, heavy slowness and a snowflake burst (5s cooldown).
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
