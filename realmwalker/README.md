# Realmwalker

A NeoForge mod for **Minecraft 1.21.1** (NeoForge 21.1.x, Java 21). Five adventure dimensions behind
Waystone portals — each with its own biomes, blocks, 5 mobs, 7 items, a boss and a ~30 minute quest
chain — plus a peaceful reward dimension, an in-game guide book (the Wayfarer's Codex), advancements,
and a config file. Total target playtime: 2-3 hours, hard but fair.

## Build

Requires JDK 21 (the Gradle foojay toolchain plugin will fetch one automatically if missing).

```bash
./gradlew build        # produces build/libs/realmwalker-1.0.0.jar
./gradlew runClient    # playtest in dev
./gradlew runServer    # dedicated dev server
./gradlew runData      # data generation run config (outputs to src/generated/resources)
```

Drop the jar into the `mods` folder of a NeoForge 1.21.1 install.

## How to play

1. Find a **Waystone Shrine** in the Overworld (small stone-brick structure) and loot it for a
   **Waystone Shard** and the **Wayfarer's Codex** (or craft the Codex from a book + amethyst shard).
2. Craft **Waystone Bricks** (stone bricks + amethyst shard + quartz) and build a **4 wide x 5 high
   frame** with a 2x3 hollow interior.
3. Craft a **Wayfarer's Ember** (glowstone dust + redstone + ender pearl). Combine it with a Waystone
   Shard to make the first **Spark**, then use the Spark on the frame to light the portal.
4. Each later realm's Spark is crafted from the **previous boss's drop** + a Wayfarer's Ember.
   Portals to later realms refuse to light until you hold the previous boss advancement
   (config `requirePreviousBoss`).
5. Stepping into a lit portal lands you on a Waystone arrival platform with a return portal.
   Follow the quest chain (tracked in the advancement tab and the Codex), find the arena and its key,
   and slay the boss. Defeat all five bosses to trigger **Ascension** and open a portal to the
   reward realm at your spawn.

## Config (`config/realmwalker-common.toml`)

| Setting | Default | Effect |
| --- | --- | --- |
| `requirePreviousBoss` | `true` | Portals to a realm need the previous boss advancement (checked server-side) |
| `sharedProgress` | `false` | `true` = quest progress shared by all players on the server |
| `bossHealthMultiplier` | `1.0` | Scales every boss's max health |
| `enableColdMeter` | `true` | Enables the freezing cold meter in the frozen realm |

## Project layout

```
src/main/java/com/realmwalker/
  Realmwalker.java            mod entry point
  ModDimensions / ModBiomes   dimension + biome keys
  portal/                     Waystone frame detection + lighting
  block/                      portal, arena heart, quest blocks, hazard blocks
  item/                       sparks, codex, tools, consumables, teleporter
  entity/                     25 mobs + 5 bosses (+ projectiles), per-dimension packages
  worldgen/feature/           code-generated shrines, arenas, the Everbloom home
  registry/                   deferred registries, attributes, spawn placements
  event/                      quest tracking, cold meter, storm lightning, ascension
  client/                     renderers + Codex screen
src/main/resources/
  data/realmwalker/           dimensions, biomes, noise settings, features, advancements,
                              loot tables, recipes, tags
  assets/realmwalker/         lang, models, blockstates, sounds, placeholder textures
```

## Notes

- All textures are **script-generated placeholders** in the per-dimension palettes. See
  `TEXTURE_CHECKLIST.md` for the list of items that need real deep-pixels.com textures.
- Mob models reuse vanilla geometry with custom textures; bosses are scaled vanilla models.
- Sounds reuse vanilla sound events mapped to custom sound events in `sounds.json`.
- The mod cannot be playtested by the author; please report bugs from in-game testing.
