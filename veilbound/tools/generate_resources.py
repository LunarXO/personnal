#!/usr/bin/env python3
"""Generates all static resources for Veilbound (textures, models, blockstates,
lang, loot tables, recipes, tags, advancements, worldgen JSON) into
src/main/resources. Run from the veilbound/ project root:

    python3 tools/generate_resources.py

Textures are placeholder art (deterministic noise + shapes) so the mod is
fully playable; replace the PNGs under src/main/resources/assets/veilbound/textures
with real art later.
"""
import json
import os
import random
import struct
import zlib

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources")
ASSETS = os.path.join(ROOT, "assets", "veilbound")
DATA = os.path.join(ROOT, "data")

# ---------------------------------------------------------------- PNG writer

def write_png(path, w, h, pixels):
    """pixels: list of rows, each a list of (r, g, b, a) tuples."""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    raw = b""
    for row in pixels:
        raw += b"\x00" + b"".join(struct.pack("4B", *px) for px in row)

    def chunk(tag, data):
        c = struct.pack(">I", len(data)) + tag + data
        return c + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, 9))
    png += chunk(b"IEND", b"")
    with open(path, "wb") as f:
        f.write(png)


def paint(path, w, h, fn):
    rng = random.Random(os.path.basename(path))
    px = [[fn(x, y, rng) for x in range(w)] for y in range(h)]
    write_png(path, w, h, px)


def noisy(base, vary=18):
    if isinstance(base, int):
        base = (base >> 16 & 255, base >> 8 & 255, base & 255)
    def fn(x, y, rng):
        v = rng.randint(-vary, vary)
        return (max(0, min(255, base[0] + v)), max(0, min(255, base[1] + v)),
                max(0, min(255, base[2] + v)), 255)
    return fn


def ore_tex(base, speckle, speckles=28, seed="ore"):
    def fn(x, y, rng):
        v = rng.randint(-14, 14)
        c = (max(0, min(255, base[0] + v)), max(0, min(255, base[1] + v)), max(0, min(255, base[2] + v)), 255)
        return c
    paint_rng = random.Random(seed + "spots")
    spots = {(paint_rng.randrange(16), paint_rng.randrange(16)) for _ in range(speckles)}
    def wrapper(x, y, rng):
        if (x, y) in spots:
            return (*speckle, 255)
        return fn(x, y, rng)
    return wrapper


def item_icon(base, accent=None):
    """16x16 item icon: filled rounded square with accent border."""
    def fn(x, y, rng):
        v = rng.randint(-12, 12)
        c = (max(0, min(255, base[0] + v)), max(0, min(255, base[1] + v)), max(0, min(255, base[2] + v)))
        if accent and (x in (1, 14) or y in (1, 14)):
            c = accent
        return (*c, 255)
    return fn


def tool_icon(base, blade):
    """Simple diagonal tool shape on 16x16."""
    def fn(x, y, rng):
        if 3 <= x + (15 - y) <= 5 or 4 <= x + (15 - y) <= 6 and y < 10:
            v = rng.randint(-10, 10)
            return (max(0, min(255, blade[0] + v)), max(0, min(255, blade[1] + v)), max(0, min(255, blade[2] + v)), 255)
        if x - y == 8 or x - y == 9:
            return (*base, 255)
        return (0, 0, 0, 0)
    return fn


# ---------------------------------------------------------------- tables

SIMPLE_BLOCKS = {
    "veilstone": (0x4A3A6B, "stone"), "cobbled_veilstone": (0x3E2F59, "stone"),
    "veilstone_bricks": (0x54438A, "bricks"), "chiseled_veilstone": (0x5D4C96, "bricks"),
    "voidstone": (0x14101F, "stone"), "voidstone_bricks": (0x1C1530, "bricks"),
    "aetherite_ore": (0x3E2F59, "ore:60E8FF"), "deepslate_aetherite_ore": (0x23203A, "ore:60E8FF"),
    "voidstone_ore": (0x3E2F59, "ore:B04FFF"), "glimmer_shard_ore": (0x3E2F59, "ore:FF8FE0"),
    "glimmer_crystal_block": (0xFF8FE0, "crystal"), "raw_aetherite_block": (0x60E8FF, "metal"),
    "raw_voidstone_block": (0xB04FFF, "metal"), "veil_portal_frame": (0x2A1A4A, "frame"),
    "veil_portal": (0xB04FFF, "portal"), "gravity_anchor": (0x60E8FF, "metal"),
    "resonant_spire": (0xFF8FE0, "crystal"), "ember_brazier": (0xFF7722, "metal"),
    "abyss_stone": (0x1A0B12, "stone"), "abyss_portal_frame": (0x2A0A12, "frame"), "abyss_portal": (0xFF3300, "portal"),
}
COLUMN_BLOCKS = {"veil_turf": ((0x5D4C31, "top"), (0x4A3A6B, "side"))}
PLANT_BLOCKS = ["veil_grass", "veil_bloom", "hanging_veil_vines", "glowing_veil_fungus"]
STAIR_SLAB_WALL = {"veilstone_stairs": "veilstone", "veilstone_slab": "veilstone", "veilstone_wall": "veilstone"}

ITEMS = {
    "raw_aetherite": (0x60E8FF, "item"), "aetherite_ingot": (0x7DF0FF, "ingot"),
    "raw_voidstone": (0xB04FFF, "item"), "voidstone_ingot": (0xC070FF, "ingot"),
    "glimmer_shard": (0xFF8FE0, "gem"), "ember_core": (0xFF7722, "orb"),
    "void_heart": (0x8B0000, "heart"), "veil_essence": (0x9B8BD0, "orb"),
    "veilberry": (0xB06AFF, "berry"), "ember_fruit": (0xFF5500, "berry"), "gloom_stew": (0x4A3A6B, "bowl"),
    "emberbrand_sword": ((0xFF7722, 0xFFD080), "tool"), "stormcaller_greatsword": ((0x60E8FF, 0xE8FFFF), "tool"),
    "voidreaver_scythe": ((0xB04FFF, 0xE0C0FF), "tool"), "aetherfang_dagger": ((0x7DF0FF, 0xFFFFFF), "tool"),
    "ember_pickaxe": ((0x8A5A2B, 0xFF7722), "tool"), "ember_axe": ((0x8A5A2B, 0xFF7722), "tool"),
    "ember_shovel": ((0x8A5A2B, 0xFF7722), "tool"), "ember_hoe": ((0x8A5A2B, 0xFF7722), "tool"),
    "aetherite_pickaxe": ((0x8A5A2B, 0x60E8FF), "tool"), "aetherite_axe": ((0x8A5A2B, 0x60E8FF), "tool"),
    "aetherite_shovel": ((0x8A5A2B, 0x60E8FF), "tool"), "aetherite_hoe": ((0x8A5A2B, 0x60E8FF), "tool"),
    "voidsteel_pickaxe": ((0x8A5A2B, 0xB04FFF), "tool"), "voidsteel_axe": ((0x8A5A2B, 0xB04FFF), "tool"),
    "voidsteel_shovel": ((0x8A5A2B, 0xB04FFF), "tool"), "voidsteel_hoe": ((0x8A5A2B, 0xB04FFF), "tool"),
    "veilbow": ((0x4A3A6B, 0xB06AFF), "bow"), "veil_arrow": ((0xB06AFF, 0xFF8FE0), "arrow"),
    "shard_launcher": ((0x8A5A2B, 0xFF8FE0), "tool"), "gloom_wand": ((0x8A5A2B, 0x9B8BD0), "tool"),
    "storm_scepter": ((0x8A5A2B, 0x60E8FF), "tool"), "ember_staff": ((0x8A5A2B, 0xFF7722), "tool"),
    "veil_key": (0xFFD700, "key"), "abyss_key": (0xFF3300, "key"), "amulet_of_slow_fall": (0x60E8FF, "amulet"),
    "gravity_ring": (0xB04FFF, "ring"), "veil_cloak": (0x2A1A4A, "cloak"),
    "ember_helmet": (0xFF7722, "armor"), "ember_chestplate": (0xFF7722, "armor"), "ember_leggings": (0xFF7722, "armor"), "ember_boots": (0xFF7722, "armor"),
    "aetherite_helmet": (0x60E8FF, "armor"), "aetherite_chestplate": (0x60E8FF, "armor"), "aetherite_leggings": (0x60E8FF, "armor"), "aetherite_boots": (0x60E8FF, "armor"),
    "voidsteel_helmet": (0xB04FFF, "armor"), "voidsteel_chestplate": (0xB04FFF, "armor"), "voidsteel_leggings": (0xB04FFF, "armor"), "voidsteel_boots": (0xB04FFF, "armor"),
}
HANDHELD_ITEMS = {n for n, v in ITEMS.items() if v[1] == "tool"}
SPAWN_EGGS = ["glimmerfox", "shardling", "veil_moth", "hollow_husk", "veil_reaver", "ember_wisp", "crystal_golem", "hollow_king"]
BLOCK_ITEMS = [b for b in SIMPLE_BLOCKS if b not in ("veil_portal", "abyss_portal")] + list(COLUMN_BLOCKS) + list(STAIR_SLAB_WALL) + PLANT_BLOCKS + ["veil_portal_frame", "abyss_portal_frame"]
# veil_portal has no item

ENTITIES = {
    "glimmerfox": (64, 32, (0xB06AFF,)), "shardling": (64, 32, (0x7FE0D0,)), "veil_moth": (64, 32, (0x9B8BD0,)),
    "hollow_husk": (64, 64, (0x6B4FA0,)), "veil_reaver": (64, 64, (0xC04040,)), "ember_wisp": (64, 32, (0xFF7722,)),
    "crystal_golem": (128, 128, (0x8FE8FF,)), "hollow_king": (64, 64, (0xB04FFF,)),
}
ARMOR_MATS = ["ember", "aetherite", "voidsteel"]
ARMOR_COLORS = {"ember": 0xFF7722, "aetherite": 0x60E8FF, "voidsteel": 0xB04FFF}

SOUNDS = ["portal_open", "portal_travel", "portal_ambient", "shard_shoot", "gloom_cast", "wisp_shoot", "boss_roar", "storm_cast", "ember_cast"]


def jwrite(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


def title(name):
    return " ".join(w.capitalize() for w in name.split("_"))


# ---------------------------------------------------------------- textures

def gen_textures():
    for name, spec in SIMPLE_BLOCKS.items():
        base, kind = spec
        b = tuple((base >> 16 & 255, base >> 8 & 255, base & 255))
        if kind.startswith("ore:"):
            sp = tuple(int(kind[4+i:4+i+2], 16) for i in (0, 2, 4))
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, ore_tex(b, sp, seed=name))
        elif kind == "bricks":
            def fn(x, y, rng, b=b):
                mortar = (y % 4 == 3) or (x % 8 == 7 and (y // 4) % 2 == 0) or (x % 8 == 3 and (y // 4) % 2 == 1)
                if mortar:
                    return (b[0] // 2, b[1] // 2, b[2] // 2, 255)
                v = rng.randint(-12, 12)
                return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, fn)
        elif kind == "frame":
            def fn(x, y, rng, b=b):
                border = x in (0, 15) or y in (0, 15) or (4 <= x <= 11 and 4 <= y <= 11 and (x in (4, 11) or y in (4, 11)))
                if border:
                    return (0xB0, 0x4F, 0xFF, 255)
                v = rng.randint(-10, 10)
                return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, fn)
        elif kind == "portal":
            def fn(x, y, rng):
                d = max(abs(x - 7.5), abs(y - 7.5))
                v = rng.randint(-30, 30)
                r = max(0, min(255, 0xB0 + v)); g = max(0, min(255, 0x4F + v)); bl = max(0, min(255, 0xFF + v // 2))
                if d > 7:
                    return (r // 3, g // 3, bl // 3, 255)
                return (r, g, bl, 255)
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, fn)
        elif kind == "crystal":
            def fn(x, y, rng, b=b):
                if (x + y) % 4 == 0 or (x - y) % 5 == 0:
                    return (255, 255, 255, 255)
                v = rng.randint(-20, 20)
                return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, fn)
        else:
            paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, noisy(b))
    for name, (top, side) in COLUMN_BLOCKS.items():
        tb = tuple((top[0] >> 16 & 255, top[0] >> 8 & 255, top[0] & 255))
        sb = tuple((side[0] >> 16 & 255, side[0] >> 8 & 255, side[0] & 255))
        paint(f"{ASSETS}/textures/block/{name}_top.png", 16, 16, noisy(tb, 10))
        paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, noisy(sb))
    plant_colors = {"veil_grass": 0x7A5A9E, "veil_bloom": 0xFF8FE0, "hanging_veil_vines": 0x5D4C96, "glowing_veil_fungus": 0x9BFFE0}
    for name in PLANT_BLOCKS:
        c = plant_colors[name]
        b = (c >> 16 & 255, c >> 8 & 255, c & 255)
        def fn(x, y, rng, b=b):
            if (x * 7 + y * 13) % 5 == 0 or (x + y * 3) % 6 == 0:
                v = rng.randint(-30, 30)
                return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
            return (0, 0, 0, 0)
        paint(f"{ASSETS}/textures/block/{name}.png", 16, 16, fn)
    for name, spec in ITEMS.items():
        base, kind = spec
        c0 = base if isinstance(base, int) else base[0]
        b = (c0 >> 16 & 255, c0 >> 8 & 255, c0 & 255)
        if kind == "tool":
            paint(f"{ASSETS}/textures/item/{name}.png", 16, 16, tool_icon((0x8A, 0x5A, 0x2B), b))
        elif kind == "bow":
            def fn(x, y, rng, b=b):
                if 2 <= x <= 3 and y < 14 and abs(y - x) < 3 or (x in (2, 3) and 2 <= y <= 13):
                    return (*b, 255)
                if x in (2, 3) or (y in (2, 3) and x < 14):
                    return (*b, 255)
                return (0, 0, 0, 0)
            paint(f"{ASSETS}/textures/item/{name}.png", 16, 16, fn)
        elif kind in ("key", "ring", "amulet", "heart", "orb", "gem"):
            def fn(x, y, rng, b=b, kind=kind):
                cx, cy = 7.5, 7.5
                d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
                if kind == "key" and x in (6, 7, 8) and 2 <= y <= 12:
                    return (*b, 255)
                if kind == "ring" and 4.5 <= d <= 6.5:
                    return (*b, 255)
                if kind == "amulet" and 3 <= d <= 6 and y >= 5:
                    return (*b, 255)
                if kind in ("heart", "orb", "gem") and d <= 5.5:
                    v = rng.randint(-20, 20)
                    return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
                return (0, 0, 0, 0)
            paint(f"{ASSETS}/textures/item/{name}.png", 16, 16, fn)
        else:
            accent = (255, 255, 255) if kind in ("ingot", "armor", "arrow", "cloak", "bowl", "berry") else None
            paint(f"{ASSETS}/textures/item/{name}.png", 16, 16, item_icon(b, accent))
    for ent, (w, h, (c,)) in ENTITIES.items():
        b = (c >> 16 & 255, c >> 8 & 255, c & 255)
        def fn(x, y, rng, b=b, w=w, h=h):
            v = rng.randint(-16, 16)
            edge = x in (0, w - 1) or y in (0, h - 1)
            if edge:
                return (b[0] // 3, b[1] // 3, b[2] // 3, 255)
            return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
        paint(f"{ASSETS}/textures/entity/{ent}.png", w, h, fn)
    paint(f"{ASSETS}/textures/entity/veil_arrow.png", 32, 32, noisy(0xB06AFF))
    for mat in ARMOR_MATS:
        c = ARMOR_COLORS[mat]
        b = (c >> 16 & 255, c >> 8 & 255, c & 255)
        for layer in (1, 2):
            def fn(x, y, rng, b=b):
                v = rng.randint(-10, 10)
                return (max(0, min(255, b[0] + v)), max(0, min(255, b[1] + v)), max(0, min(255, b[2] + v)), 255)
            paint(f"{ASSETS}/textures/entity/armor/{mat}_layer_{layer}.png", 64, 32, fn)
    for eff, c in (("ember_mark", 0xFF5500), ("gravity_well", 0x8A2BE2)):
        b = (c >> 16 & 255, c >> 8 & 255, c & 255)
        paint(f"{ASSETS}/textures/mob_effect/{eff}.png", 18, 18, noisy(b, 24))
    paint(f"{ASSETS}/textures/particle/shard_sparkle.png", 8, 8, lambda x, y, rng: (255, 200, 255, 255) if (x + y) % 2 == 0 else (176, 79, 255, 200))


# ---------------------------------------------------------------- models / blockstates

def gen_models():
    for name in SIMPLE_BLOCKS:
        jwrite(f"{ASSETS}/models/block/{name}.json", {
            "parent": "minecraft:block/cube_all",
            "textures": {"all": f"veilbound:block/{name}", "particle": f"#all",
                         "down": "#all", "up": "#all", "north": "#all", "east": "#all", "south": "#all", "west": "#all"},
        })
        jwrite(f"{ASSETS}/blockstates/{name}.json", {"variants": {"": {"model": f"veilbound:block/{name}"}}})
    for name in COLUMN_BLOCKS:
        jwrite(f"{ASSETS}/models/block/{name}.json", {
            "parent": "minecraft:block/cube_column",
            "textures": {"end": f"veilbound:block/{name}_top", "side": f"veilbound:block/{name}"},
        })
        jwrite(f"{ASSETS}/blockstates/{name}.json", {"variants": {"": {"model": f"veilbound:block/{name}"}}})
    for name in PLANT_BLOCKS:
        jwrite(f"{ASSETS}/models/block/{name}.json", {"parent": "minecraft:block/cross", "textures": {"cross": f"veilbound:block/{name}"}})
        jwrite(f"{ASSETS}/blockstates/{name}.json", {"variants": {"": {"model": f"veilbound:block/{name}"}}})
    # stairs / slab / wall from vanilla templates (normalized into tools/templates)
    tpl_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "templates")
    for name in STAIR_SLAB_WALL:
        prefix, kind = name.rsplit("_", 1)
        bs = json.load(open(f"{tpl_dir}/blockstates/{kind}.json"))
        def fix_models(o):
            if isinstance(o, dict):
                for k, v in o.items():
                    if k == "model" and isinstance(v, str):
                        if kind == "wall":
                            o[k] = v.replace("minecraft:block/cobblestone_wall", f"veilbound:block/{name}")
                        else:
                            o[k] = v.replace("minecraft:block/stone_", f"veilbound:block/{prefix}_")
                    else:
                        fix_models(v)
            elif isinstance(o, list):
                for v in o:
                    fix_models(v)
        fix_models(bs)
        jwrite(f"{ASSETS}/blockstates/{name}.json", bs)
        if kind == "stairs":
            models = [("stairs", "stairs"), ("inner", "stairs_inner"), ("outer", "stairs_outer")]
        elif kind == "slab":
            models = [("slab", "slab"), ("slab_top", "slab_top")]
        else:
            models = [("post", "wall_post"), ("side", "wall_side"), ("side_tall", "wall_side_tall"), ("inventory", "wall_inventory")]
        for tpl, outname in models:
            src = json.load(open(f"{tpl_dir}/models/{kind}/{tpl}.json"))
            s = json.dumps(src)
            if kind == "wall":
                s = s.replace("minecraft:block/cobblestone_wall", f"veilbound:block/{prefix}")
            else:
                s = s.replace("minecraft:block/stone", f"veilbound:block/{prefix}")
            jwrite(f"{ASSETS}/models/block/{prefix}_{outname}.json", json.loads(s))
    # item models
    for name in BLOCK_ITEMS:
        block_model = f"{name}_inventory" if name in STAIR_SLAB_WALL and name.endswith("_wall") else name
        jwrite(f"{ASSETS}/models/item/{name}.json", {"parent": f"veilbound:block/{block_model}"})
    for name in ITEMS:
        parent = "minecraft:item/handheld" if name in HANDHELD_ITEMS else "minecraft:item/generated"
        jwrite(f"{ASSETS}/models/item/{name}.json", {"parent": parent, "textures": {"layer0": f"veilbound:item/{name}"}})
    for egg in SPAWN_EGGS:
        jwrite(f"{ASSETS}/models/item/{egg}_spawn_egg.json", {"parent": "minecraft:item/template_spawn_egg"})


# ---------------------------------------------------------------- lang / sounds / particles / atlas

def gen_misc_assets():
    lang = {"itemGroup.veilbound.veilbound_tab": "Veilbound"}
    for name in SIMPLE_BLOCKS:
        lang[f"block.veilbound.{name}"] = title(name)
    for name in COLUMN_BLOCKS:
        lang[f"block.veilbound.{name}"] = title(name)
    for name in PLANT_BLOCKS + list(STAIR_SLAB_WALL):
        lang[f"block.veilbound.{name}"] = title(name)
    for name in ITEMS:
        lang[f"item.veilbound.{name}"] = title(name)
    for egg in SPAWN_EGGS:
        lang[f"item.veilbound.{egg}_spawn_egg"] = title(egg) + " Spawn Egg"
        lang[f"entity.veilbound.{egg}"] = title(egg)
    lang["effect.veilbound.ember_mark"] = "Ember Mark"
    lang["effect.veilbound.gravity_well"] = "Gravity Well"
    lang["advancements.veilbound.root.title"] = "Into the Veil"
    lang["advancements.veilbound.root.description"] = "Mine Veilstone"
    lang["advancements.veilbound.enter_the_veil.title"] = "Shattered Sky"
    lang["advancements.veilbound.enter_the_veil.description"] = "Enter the Veil"
    lang["advancements.veilbound.slay_hollow_king.title"] = "King of Nothing"
    lang["advancements.veilbound.slay_hollow_king.description"] = "Defeat the Hollow King"
    lang["advancements.veilbound.geared_up.title"] = "Aetherite-Plated"
    lang["advancements.veilbound.geared_up.description"] = "Craft an Aetherite Chestplate"
    jwrite(f"{ASSETS}/lang/en_us.json", lang)
    sounds = {name: {"sounds": [{"name": f"veilbound:{name}"}]} for name in SOUNDS}
    jwrite(f"{ASSETS}/sounds.json", sounds)
    jwrite(f"{ASSETS}/particles/shard_sparkle.json", {"textures": ["veilbound:shard_sparkle"]})
    jwrite(f"{ASSETS}/atlases/mob_effects.json", {"sources": [{"type": "directory", "source": "mob_effect", "prefix": ""}]})


# ---------------------------------------------------------------- loot tables

def block_loot_self(name):
    return {"type": "minecraft:block", "pools": [{"bonus_rolls": 0.0, "rolls": 1.0, "entries": [{"type": "minecraft:item", "name": f"veilbound:{name}"}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]}]}


def block_loot_ore(ore, drop, count=None):
    entry = {"type": "minecraft:item", "name": f"veilbound:{drop}",
             "functions": [{"function": "minecraft:explosion_decay"}]}
    if count:
        entry["functions"].insert(0, {"function": "minecraft:set_count", "count": count, "add": False})
    entry["functions"].insert(0 if not count else 1, {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"})
    return {"type": "minecraft:block", "pools": [{"bonus_rolls": 0.0, "rolls": 1.0, "entries": [
        {"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "name": f"veilbound:{ore}", "conditions": [
                {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}]},
            entry]}]}]}


def gen_block_loot():
    out = os.path.join(DATA, "veilbound", "loot_table", "blocks")
    for name in SIMPLE_BLOCKS:
        if name in ("veil_portal", "abyss_portal"):
            continue  # portal blocks have no item and noLootTable
        if name in ("aetherite_ore", "deepslate_aetherite_ore"):
            jwrite(f"{out}/{name}.json", block_loot_ore(name, "raw_aetherite"))
        elif name == "voidstone_ore":
            jwrite(f"{out}/{name}.json", block_loot_ore(name, "raw_voidstone"))
        elif name == "glimmer_shard_ore":
            jwrite(f"{out}/{name}.json", block_loot_ore(name, "glimmer_shard", {"type": "minecraft:uniform", "min": 2.0, "max": 4.0}))
        elif name == "glimmer_crystal_block":
            jwrite(f"{out}/{name}.json", block_loot_ore(name, "glimmer_shard", {"type": "minecraft:uniform", "min": 2.0, "max": 4.0}))
        elif name == "veilstone":
            loot = block_loot_self("cobbled_veilstone")
            jwrite(f"{out}/{name}.json", loot)
        else:
            jwrite(f"{out}/{name}.json", block_loot_self(name))
    for name in COLUMN_BLOCKS:
        jwrite(f"{out}/{name}.json", block_loot_self(name))
    for name in PLANT_BLOCKS + list(STAIR_SLAB_WALL):
        jwrite(f"{out}/{name}.json", block_loot_self(name))


def entity_loot(pools):
    return {"type": "minecraft:entity", "pools": pools}


def pool(entries, rolls=1.0, conditions=None):
    p = {"bonus_rolls": 0.0, "rolls": rolls, "entries": entries}
    if conditions:
        p["conditions"] = conditions
    return p


def item_entry(name, min_c=1, max_c=1, chance=None, looting=False):
    e = {"type": "minecraft:item", "name": f"veilbound:{name}" if not name.startswith("minecraft:") else name}
    fns = []
    if max_c > min_c:
        fns.append({"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": float(min_c), "max": float(max_c)}, "add": False})
    if looting:
        fns.append({"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting",
                    "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}})
    if fns:
        e["functions"] = fns
    return e


def chance_pool(entries, chance, rolls=1.0, killed_by_player=False):
    conds = [{"condition": "minecraft:random_chance", "chance": chance}]
    if killed_by_player:
        conds.append({"condition": "minecraft:killed_by_player"})
    return pool(entries, rolls, conds)


def gen_entity_loot():
    out = os.path.join(DATA, "veilbound", "loot_table", "entities")
    jwrite(f"{out}/glimmerfox.json", entity_loot([pool([item_entry("veilberry", 1, 2)])]))
    jwrite(f"{out}/shardling.json", entity_loot([pool([item_entry("glimmer_shard", 1, 2)])]))
    jwrite(f"{out}/veil_moth.json", entity_loot([chance_pool([item_entry("veil_essence", 1, 1)], 0.5)]))
    jwrite(f"{out}/hollow_husk.json", entity_loot([
        pool([item_entry("veil_essence", 0, 2)]),
        chance_pool([item_entry("ember_core", 1, 1)], 0.25, killed_by_player=True)]))
    jwrite(f"{out}/veil_reaver.json", entity_loot([
        pool([item_entry("veil_essence", 1, 3)]),
        chance_pool([item_entry("aetherite_ingot", 1, 1)], 0.3, killed_by_player=True)]))
    jwrite(f"{out}/ember_wisp.json", entity_loot([
        pool([item_entry("ember_core", 1, 1)]),
        chance_pool([item_entry("ember_core", 1, 1)], 0.5)]))
    jwrite(f"{out}/crystal_golem.json", entity_loot([
        pool([item_entry("glimmer_shard", 3, 6)]),
        chance_pool([item_entry("glimmer_crystal_block", 1, 1)], 0.5, killed_by_player=True)]))
    jwrite(f"{out}/hollow_king.json", entity_loot([
        pool([item_entry("void_heart", 1, 1)]),
        pool([item_entry("veil_essence", 2, 4)]),
        chance_pool([item_entry("aetherite_ingot", 2, 4)], 0.8, killed_by_player=True)]))


# ---------------------------------------------------------------- recipes

def shaped(result, count, pattern, key, category="misc"):
    return {"type": "minecraft:crafting_shaped", "category": category, "pattern": pattern,
            "key": {k: {"item": v} for k, v in key.items()}, "result": {"id": f"veilbound:{result}", "count": count}}


def shapeless(result, count, ingredients, category="misc"):
    return {"type": "minecraft:crafting_shapeless", "category": category,
            "ingredients": [{"item": i} for i in ingredients], "result": {"id": f"veilbound:{result}", "count": count}}


def smelting(result, ingredient, xp, time=200):
    return {"type": "minecraft:smelting", "ingredient": {"item": ingredient}, "result": {"id": f"veilbound:{result}", "count": 1},
            "experience": xp, "cookingtime": time}


def blasting(result, ingredient, xp, time=100):
    r = smelting(result, ingredient, xp, time)
    r["type"] = "minecraft:blasting"
    return r


def stonecutting(result, ingredient, count=1):
    return {"type": "minecraft:stonecutting", "ingredient": {"item": ingredient}, "result": {"id": f"veilbound:{result}", "count": count}}


def gen_recipes():
    out = os.path.join(DATA, "veilbound", "recipe")
    S, ST = "veilbound:veilstone", "minecraft:stick"
    V, VB, A = "veilbound:voidstone", "veilbound:voidstone_bricks", "veilbound:aetherite_ingot"
    G, E, VS = "veilbound:glimmer_shard", "veilbound:ember_core", "veilbound:voidstone_ingot"
    GC = "veilbound:glimmer_crystal_block"
    jwrite(f"{out}/veilstone_bricks.json", shaped("veilstone_bricks", 4, ["##", "##"], {"#": S}, "building"))
    jwrite(f"{out}/chiseled_veilstone.json", shaped("chiseled_veilstone", 1, ["#", "#"], {"#": "veilbound:veilstone_slab"}, "building"))
    jwrite(f"{out}/veilstone_stairs.json", shaped("veilstone_stairs", 4, ["#  ", "## ", "###"], {"#": S}, "building"))
    jwrite(f"{out}/veilstone_slab.json", shaped("veilstone_slab", 6, ["###"], {"#": S}, "building"))
    jwrite(f"{out}/veilstone_wall.json", shaped("veilstone_wall", 6, ["###", "###"], {"#": S}, "building"))
    jwrite(f"{out}/voidstone_bricks.json", shaped("voidstone_bricks", 4, ["##", "##"], {"#": V}, "building"))
    jwrite(f"{out}/raw_aetherite_block.json", shaped("raw_aetherite_block", 1, ["###", "###", "###"], {"#": "veilbound:raw_aetherite"}, "building"))
    jwrite(f"{out}/raw_aetherite_from_block.json", shapeless("raw_aetherite", 9, ["veilbound:raw_aetherite_block"], "building"))
    jwrite(f"{out}/raw_voidstone_block.json", shaped("raw_voidstone_block", 1, ["###", "###", "###"], {"#": "veilbound:raw_voidstone"}, "building"))
    jwrite(f"{out}/raw_voidstone_from_block.json", shapeless("raw_voidstone", 9, ["veilbound:raw_voidstone_block"], "building"))
    jwrite(f"{out}/glimmer_crystal_block.json", shaped("glimmer_crystal_block", 1, ["##", "##"], {"#": G}, "building"))
    jwrite(f"{out}/glimmer_shard_from_block.json", shapeless("glimmer_shard", 4, [GC], "building"))
    # smelting
    jwrite(f"{out}/aetherite_ingot_from_raw.json", smelting("aetherite_ingot", "veilbound:raw_aetherite", 0.7))
    jwrite(f"{out}/aetherite_ingot_from_raw_blasting.json", blasting("aetherite_ingot", "veilbound:raw_aetherite", 0.7))
    jwrite(f"{out}/aetherite_ingot_from_ore.json", smelting("aetherite_ingot", "veilbound:aetherite_ore", 0.7))
    jwrite(f"{out}/aetherite_ingot_from_ore_blasting.json", blasting("aetherite_ingot", "veilbound:aetherite_ore", 0.7))
    jwrite(f"{out}/aetherite_ingot_from_deepslate_ore_blasting.json", blasting("aetherite_ingot", "veilbound:deepslate_aetherite_ore", 0.7))
    jwrite(f"{out}/voidstone_ingot_from_raw.json", smelting("voidstone_ingot", "veilbound:raw_voidstone", 1.0))
    jwrite(f"{out}/voidstone_ingot_from_raw_blasting.json", blasting("voidstone_ingot", "veilbound:raw_voidstone", 1.0))
    jwrite(f"{out}/voidstone_ingot_from_ore_blasting.json", blasting("voidstone_ingot", "veilbound:voidstone_ore", 1.0))
    jwrite(f"{out}/glimmer_shard_from_ore_blasting.json", blasting("glimmer_shard", "veilbound:glimmer_shard_ore", 0.4))
    # stonecutting
    for res, cnt in [("veilstone_stairs", 1), ("veilstone_slab", 2), ("veilstone_wall", 1), ("veilstone_bricks", 1), ("chiseled_veilstone", 1)]:
        jwrite(f"{out}/stonecutting/{res}.json", stonecutting(res, S, cnt))
    # tools & weapons
    mats = {"ember": E, "aetherite": A, "voidsteel": VS}
    jwrite(f"{out}/emberbrand_sword.json", shaped("emberbrand_sword", 1, ["M", "M", "S"], {"M": mats["ember"], "S": ST}, "combat"))
    for tier, mat in mats.items():
        jwrite(f"{out}/{tier}_pickaxe.json", shaped(f"{tier}_pickaxe", 1, ["MMM", " S ", " S "], {"M": mat, "S": ST}, "tools"))
        jwrite(f"{out}/{tier}_axe.json", shaped(f"{tier}_axe", 1, ["MM", "MS", " S"], {"M": mat, "S": ST}, "tools"))
        jwrite(f"{out}/{tier}_shovel.json", shaped(f"{tier}_shovel", 1, ["M", "S", "S"], {"M": mat, "S": ST}, "tools"))
        jwrite(f"{out}/{tier}_hoe.json", shaped(f"{tier}_hoe", 1, ["MM", " S", " S"], {"M": mat, "S": ST}, "tools"))
    # named weapons
    jwrite(f"{out}/stormcaller_greatsword.json", shaped("stormcaller_greatsword", 1, [" M ", " M ", " S "], {"M": A, "S": ST}, "combat"))
    jwrite(f"{out}/voidreaver_scythe.json", shaped("voidreaver_scythe", 1, [" MM", " S ", " S "], {"M": VS, "S": ST}, "combat"))
    jwrite(f"{out}/aetherfang_dagger.json", shaped("aetherfang_dagger", 1, ["A", "S"], {"A": A, "S": ST}, "combat"))
    # ranged
    jwrite(f"{out}/veilbow.json", shaped("veilbow", 1, [" SA", "S A", " SA"], {"S": ST, "A": "minecraft:string"}, "combat"))
    jwrite(f"{out}/veil_arrow.json", shaped("veil_arrow", 4, ["G", "S", "F"], {"G": G, "S": ST, "F": "minecraft:feather"}, "combat"))
    jwrite(f"{out}/shard_launcher.json", shaped("shard_launcher", 1, ["GGG", "S S", " S "], {"G": G, "S": ST}, "combat"))
    jwrite(f"{out}/gloom_wand.json", shaped("gloom_wand", 1, [" V", "SV", "S "], {"V": VS, "S": ST}, "combat"))
    jwrite(f"{out}/storm_scepter.json", shaped("storm_scepter", 1, [" A ", "AGA", " S "], {"A": A, "G": G, "S": ST}, "combat"))
    jwrite(f"{out}/ember_staff.json", shaped("ember_staff", 1, [" E ", "EVE", " S "], {"E": E, "V": V, "S": ST}, "combat"))
    jwrite(f"{out}/abyss_key.json", shaped("abyss_key", 1, [" V ", "VEV", " V "], {"V": VS, "E": E}, "misc"))
    # armor
    for tier, mat in mats.items():
        jwrite(f"{out}/{tier}_helmet.json", shaped(f"{tier}_helmet", 1, ["MMM", "M M"], {"M": mat}, "combat"))
        jwrite(f"{out}/{tier}_chestplate.json", shaped(f"{tier}_chestplate", 1, ["M M", "MMM", "MMM"], {"M": mat}, "combat"))
        jwrite(f"{out}/{tier}_leggings.json", shaped(f"{tier}_leggings", 1, ["MMM", "M M", "M M"], {"M": mat}, "combat"))
        jwrite(f"{out}/{tier}_boots.json", shaped(f"{tier}_boots", 1, ["M M", "M M"], {"M": mat}, "combat"))
    # functional blocks & trinkets
    jwrite(f"{out}/veil_portal_frame.json", shaped("veil_portal_frame", 1, ["ABA", "BEB", "ABA"], {"A": A, "B": VB, "E": E}, "building"))
    jwrite(f"{out}/veil_key.json", shaped("veil_key", 1, [" A ", "AVA", " A "], {"A": A, "V": VS}, "misc"))
    jwrite(f"{out}/gravity_anchor.json", shaped("gravity_anchor", 1, ["VGV", "GAG", "VGV"], {"V": V, "G": GC, "A": A}, "building"))
    jwrite(f"{out}/resonant_spire.json", shaped("resonant_spire", 1, [" G ", "GAG", " V "], {"G": GC, "A": A, "V": V}, "building"))
    jwrite(f"{out}/ember_brazier.json", shaped("ember_brazier", 1, [" E ", " S ", "VVV"], {"E": E, "S": ST, "V": V}, "building"))
    jwrite(f"{out}/amulet_of_slow_fall.json", shaped("amulet_of_slow_fall", 1, [" G ", "GAG", " G "], {"G": G, "A": A}, "misc"))
    jwrite(f"{out}/gravity_ring.json", shaped("gravity_ring", 1, [" G ", "G G", " G "], {"G": G}, "misc"))
    jwrite(f"{out}/veil_cloak.json", shaped("veil_cloak", 1, ["S S", "SVS", "VVV"], {"S": "minecraft:string", "V": V}, "misc"))
    jwrite(f"{out}/gloom_stew.json", shapeless("gloom_stew", 1, ["minecraft:bowl", "veilbound:veilberry", "veilbound:ember_fruit"], "food"))
    jwrite(f"{out}/ember_fruit.json", shapeless("ember_fruit", 1, ["veilbound:veilberry", "minecraft:blaze_powder"], "food"))


# ---------------------------------------------------------------- tags

def gen_tags():
    pickaxe = [f"veilbound:{n}" for n in list(SIMPLE_BLOCKS) + list(COLUMN_BLOCKS) + list(STAIR_SLAB_WALL)]
    jwrite(f"{DATA}/minecraft/tags/block/mineable/pickaxe.json", {"replace": False, "values": pickaxe})
    jwrite(f"{DATA}/minecraft/tags/block/needs_stone_tool.json", {"replace": False, "values": [
        "veilbound:aetherite_ore", "veilbound:deepslate_aetherite_ore", "veilbound:glimmer_shard_ore"]})
    jwrite(f"{DATA}/minecraft/tags/block/needs_iron_tool.json", {"replace": False, "values": [
        "veilbound:voidstone_ore", "veilbound:voidstone", "veilbound:voidstone_bricks"]})
    jwrite(f"{DATA}/veilbound/tags/block/veilstone_ore_replaceables.json", {"replace": False, "values": [
        "veilbound:veilstone", "veilbound:cobbled_veilstone", "veilbound:veil_turf", "veilbound:abyss_stone"]})
    jwrite(f"{DATA}/veilbound/tags/worldgen/biome/is_veil.json", {"replace": False, "values": [
        "veilbound:veil_expanse", "veilbound:crystal_hollows", "veilbound:ember_mires", "veilbound:abyss_wastes"]})
    jwrite(f"{DATA}/veilbound/tags/worldgen/biome/has_structure/veil_ruin.json", {"replace": False, "values": [
        "veilbound:veil_expanse", "veilbound:crystal_hollows", "veilbound:ember_mires", "veilbound:abyss_wastes"]})


# ---------------------------------------------------------------- advancements

def gen_advancements():
    out = os.path.join(DATA, "veilbound", "advancement")
    jwrite(f"{out}/root.json", {
        "criteria": {"veilstone": {"trigger": "minecraft:inventory_changed",
                                   "conditions": {"items": [{"items": "veilbound:veilstone"}]}}},
        "display": {"icon": {"id": "veilbound:veilstone", "count": 1},
                    "title": {"translate": "advancements.veilbound.root.title"},
                    "description": {"translate": "advancements.veilbound.root.description"},
                    "background": "minecraft:textures/gui/advancements/backgrounds/stone.png",
                    "frame": "task", "announce_to_chat": True, "show_toast": True, "hidden": False},
        "requirements": [["veilstone"]], "sends_telemetry_event": True})
    jwrite(f"{out}/enter_the_veil.json", {
        "parent": "veilbound:root",
        "criteria": {"entered": {"trigger": "minecraft:changed_dimension", "conditions": {"to": "veilbound:the_veil"}}},
        "display": {"icon": {"id": "veilbound:veil_key", "count": 1},
                    "title": {"translate": "advancements.veilbound.enter_the_veil.title"},
                    "description": {"translate": "advancements.veilbound.enter_the_veil.description"},
                    "frame": "goal", "announce_to_chat": True, "show_toast": True, "hidden": False},
        "requirements": [["entered"]], "sends_telemetry_event": True})
    jwrite(f"{out}/slay_hollow_king.json", {
        "parent": "veilbound:enter_the_veil",
        "criteria": {"killed": {"trigger": "minecraft:player_killed_entity",
                                "conditions": {"entity": {"type": "veilbound:hollow_king"}}}},
        "display": {"icon": {"id": "veilbound:void_heart", "count": 1},
                    "title": {"translate": "advancements.veilbound.slay_hollow_king.title"},
                    "description": {"translate": "advancements.veilbound.slay_hollow_king.description"},
                    "frame": "challenge", "announce_to_chat": True, "show_toast": True, "hidden": False},
        "requirements": [["killed"]], "sends_telemetry_event": True})
    jwrite(f"{out}/geared_up.json", {
        "parent": "veilbound:root",
        "criteria": {"chestplate": {"trigger": "minecraft:inventory_changed",
                                    "conditions": {"items": [{"items": "veilbound:aetherite_chestplate"}]}}},
        "display": {"icon": {"id": "veilbound:aetherite_chestplate", "count": 1},
                    "title": {"translate": "advancements.veilbound.geared_up.title"},
                    "description": {"translate": "advancements.veilbound.geared_up.description"},
                    "frame": "task", "announce_to_chat": True, "show_toast": True, "hidden": False},
        "requirements": [["chestplate"]], "sends_telemetry_event": True})


# ---------------------------------------------------------------- worldgen

def gen_worldgen():
    wg = os.path.join(DATA, "veilbound", "worldgen")
    # dimension type (based on the_end)
    jwrite(f"{DATA}/veilbound/dimension_type/the_veil.json", {
        "ambient_light": 0.1, "bed_works": False, "coordinate_scale": 1.0, "effects": "minecraft:the_end",
        "fixed_time": 18000, "has_ceiling": False, "has_raids": True, "has_skylight": False,
        "height": 384, "infiniburn": "#minecraft:infiniburn_overworld", "logical_height": 384,
        "min_y": -64, "monster_spawn_block_light_limit": 0,
        "monster_spawn_light_level": {"type": "minecraft:uniform", "min_inclusive": 0, "max_inclusive": 7},
        "natural": False, "piglin_safe": False, "respawn_anchor_works": False, "ultrawarm": False})
    # dimension
    jwrite(f"{DATA}/veilbound/dimension/the_veil.json", {
        "type": "veilbound:the_veil",
        "generator": {
            "type": "minecraft:noise",
            "biome_source": {"type": "minecraft:multi_noise", "biomes": [
                {"parameters": {"temperature": 0.2, "humidity": 0.0, "continentalness": 0.0, "erosion": 0.0, "depth": 0.0, "weirdness": 0.0, "offset": 0.0}, "biome": "veilbound:veil_expanse"},
                {"parameters": {"temperature": -0.4, "humidity": 0.0, "continentalness": 0.0, "erosion": 0.0, "depth": 0.0, "weirdness": 0.0, "offset": 0.0}, "biome": "veilbound:crystal_hollows"},
                {"parameters": {"temperature": 0.7, "humidity": 0.0, "continentalness": 0.0, "erosion": 0.0, "depth": 0.0, "weirdness": 0.0, "offset": 0.0}, "biome": "veilbound:ember_mires"}]},
            "settings": "veilbound:the_veil"}})
    # noise settings: patch vanilla floating_islands
    ref = os.path.join(os.path.dirname(os.path.abspath(__file__)), "templates", "floating_islands.json")
    ns = json.load(open(ref))
    ns["default_block"] = {"Name": "veilbound:veilstone"}
    ns["default_fluid"] = {"Name": "minecraft:lava", "Properties": {"level": "0"}}
    ns["noise"] = {"min_y": -64, "height": 384, "size_horizontal": 2, "size_vertical": 1}
    ns["sea_level"] = -64
    ns["surface_rule"] = {"type": "minecraft:block", "result_state": {"Name": "veilbound:veil_turf"}}
    grads = []
    def walk(o):
        if isinstance(o, dict):
            if o.get("type") == "minecraft:y_clamped_gradient":
                grads.append(o)
            for v in o.values():
                walk(v)
        elif isinstance(o, list):
            for v in o:
                walk(v)
    walk(ns["noise_router"])
    if len(grads) >= 2:
        grads[0]["from_y"], grads[0]["to_y"] = -24, 48
        grads[1]["from_y"], grads[1]["to_y"] = 96, 200
    jwrite(f"{wg}/noise_settings/the_veil.json", ns)
    # biomes
    def biome(name, sky, fog, water, water_fog, monsters, creatures, ambient, features_ores, features_deco):
        b = {
            "carvers": {}, "downfall": 0.0, "has_precipitation": False, "temperature": 0.5,
            "effects": {"fog_color": fog, "sky_color": sky, "water_color": water, "water_fog_color": water_fog,
                        "mood_sound": {"block_search_extent": 8, "offset": 2.0, "sound": "minecraft:ambient.cave", "tick_delay": 6000}},
            "spawn_costs": {},
            "spawners": {"ambient": ambient, "axolotls": [], "creature": creatures, "misc": [],
                         "monster": monsters, "underground_water_creature": [], "water_ambient": [], "water_creature": []},
            "features": [[], [], [], [], [], [], features_ores, features_deco, [], [], []],
        }
        jwrite(f"{wg}/biome/{name}.json", b)
    def spawn(t, w, mn, mx):
        return {"type": f"veilbound:{t}", "weight": w, "minCount": mn, "maxCount": mx}
    biome("veil_expanse", 0x140A24, 0x0D0618, 0x2A1A4A, 0x1A0F30,
          [spawn("hollow_husk", 40, 1, 3), spawn("veil_reaver", 15, 1, 2), spawn("ember_wisp", 20, 1, 2)],
          [spawn("glimmerfox", 8, 2, 4), spawn("shardling", 10, 2, 3)], [spawn("veil_moth", 10, 1, 3)],
          ["veilbound:ore_aetherite", "veilbound:ore_glimmer_shard"], [])
    biome("crystal_hollows", 0x1E2A4A, 0x101A33, 0x2A1A4A, 0x1A0F30,
          [spawn("crystal_golem", 10, 1, 1), spawn("hollow_husk", 30, 1, 2), spawn("ember_wisp", 15, 1, 2)],
          [spawn("shardling", 12, 2, 4)], [spawn("veil_moth", 8, 1, 2)],
          ["veilbound:ore_glimmer_shard", "veilbound:ore_aetherite_deep"], ["veilbound:crystal_cluster"])
    biome("ember_mires", 0x331A0A, 0x200D05, 0xFF6600, 0xCC4400,
          [spawn("ember_wisp", 35, 2, 4), spawn("veil_reaver", 20, 1, 2), spawn("hollow_husk", 25, 1, 3)],
          [spawn("glimmerfox", 4, 1, 2)], [spawn("veil_moth", 6, 1, 2)],
          ["veilbound:ore_voidstone", "veilbound:ore_aetherite"], [])
    # configured features
    def ore_feature(name, state, size):
        jwrite(f"{wg}/configured_feature/{name}.json", {
            "type": "minecraft:ore",
            "config": {"size": size, "discard_chance_on_air_exposure": 0.0,
                       "targets": [{"state": {"Name": f"veilbound:{state}"},
                                    "target": {"predicate_type": "minecraft:tag_match", "tag": "veilbound:veilstone_ore_replaceables"}}]}})
    ore_feature("ore_aetherite", "aetherite_ore", 10)
    ore_feature("ore_aetherite_deep", "deepslate_aetherite_ore", 8)
    ore_feature("ore_voidstone", "voidstone_ore", 8)
    ore_feature("ore_glimmer_shard", "glimmer_shard_ore", 12)
    ore_feature("crystal_cluster", "glimmer_crystal_block", 6)
    # placed features
    def placed(name, count, ymin, ymax):
        jwrite(f"{wg}/placed_feature/{name}.json", {
            "feature": f"veilbound:{name}",
            "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
                          {"type": "minecraft:height_range", "height": {"type": "minecraft:trapezoid",
                           "min_inclusive": {"absolute": ymin}, "max_inclusive": {"absolute": ymax}}},
                          {"type": "minecraft:biome"}]})
    placed("ore_aetherite", 14, -48, 120)
    placed("ore_aetherite_deep", 8, -64, 32)
    placed("ore_voidstone", 8, -48, 80)
    placed("ore_glimmer_shard", 16, -32, 144)
    placed("crystal_cluster", 6, 0, 160)



# ---------------------------------------------------------------- NBT + structures

TAG_BYTE, TAG_INT, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY = 1, 3, 8, 9, 10, 11


def _nbt_payload(f, t, v):
    if t == TAG_BYTE:
        f.write(struct.pack(">b", v))
    elif t == TAG_INT:
        f.write(struct.pack(">i", v))
    elif t == TAG_STRING:
        b = v.encode()
        f.write(struct.pack(">H", len(b)) + b)
    elif t == TAG_INT_ARRAY:
        f.write(struct.pack(">i", len(v)))
        for x in v:
            f.write(struct.pack(">i", x))
    elif t == TAG_LIST:
        et, items = v
        f.write(struct.pack(">b", et))
        f.write(struct.pack(">i", len(items)))
        for it in items:
            _nbt_payload(f, et, it)
    elif t == TAG_COMPOUND:
        for name, (tt, vv) in v.items():
            f.write(struct.pack(">b", tt))
            nb = name.encode()
            f.write(struct.pack(">H", len(nb)) + nb)
            _nbt_payload(f, tt, vv)
        f.write(b"\x00")


def write_nbt(path, root):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    import gzip
    with gzip.open(path, "wb", compresslevel=9) as f:
        f.write(struct.pack(">b", TAG_COMPOUND))
        f.write(struct.pack(">H", 0))
        _nbt_payload(f, TAG_COMPOUND, root)


def structure_nbt(path, size, palette, blocks):
    """palette: list of state names; blocks: list of (pos, state_index, nbt_or_None)."""
    pal = [{"Name": (TAG_STRING, name)} for name in palette]
    blist = []
    for pos, state, nbt in blocks:
        entry = {"pos": (TAG_LIST, (TAG_INT, list(pos))), "state": (TAG_INT, state)}
        if nbt:
            entry["nbt"] = (TAG_COMPOUND, nbt)
        blist.append(entry)
    root = {
        "DataVersion": (TAG_INT, 3955),
        "size": (TAG_LIST, (TAG_INT, list(size))),
        "palette": (TAG_LIST, (TAG_COMPOUND, pal)),
        "blocks": (TAG_LIST, (TAG_COMPOUND, blist)),
        "entities": (TAG_LIST, (TAG_COMPOUND, [])),
    }
    write_nbt(path, root)


def gen_structures():
    out = os.path.join(DATA, "veilbound")
    # empty template for game tests
    structure_nbt(f"{out}/structure/empty3.nbt", (3, 3, 3), ["minecraft:air"], [])
    # veil ruin: 13x10x13, floor + corner pillars + partial walls + loot chest + decorations
    palette = [
        "veilbound:veilstone_bricks", "veilbound:cobbled_veilstone", "veilbound:chiseled_veilstone",
        "veilbound:veilstone_wall", "minecraft:chest", "veilbound:glimmer_crystal_block",
        "veilbound:ember_brazier", "veilbound:voidstone_bricks",
    ]
    idx = {name: i for i, name in enumerate(palette)}
    rng = random.Random("veil_ruin")
    blocks = []
    W = 13
    for x in range(W):
        for z in range(W):
            name = "veilbound:cobbled_veilstone" if rng.random() < 0.2 else "veilbound:veilstone_bricks"
            blocks.append(((x, 0, z), idx[name], None))
    for cx, cz in ((1, 1), (W - 2, 1), (1, W - 2), (W - 2, W - 2)):
        for y in range(1, 7):
            blocks.append(((cx, y, cz), idx["veilbound:chiseled_veilstone"], None))
    for y in range(1, 4):
        for x in range(1, W - 1):
            if (x % 3 != 0 or y > 1):
                blocks.append(((x, y, 0), idx["veilbound:veilstone_wall"], None))
                blocks.append(((x, y, W - 1), idx["veilbound:veilstone_wall"], None))
        for z in range(1, W - 1):
            if (z % 4 != 1 or y > 1):
                blocks.append(((0, y, z), idx["veilbound:veilstone_wall"], None))
                blocks.append(((W - 1, y, z), idx["veilbound:veilstone_wall"], None))
    for dx, dz in ((5, 5), (7, 5), (5, 7), (7, 7)):
        blocks.append(((dx, 1, dz), idx["veilbound:glimmer_crystal_block"], None))
    blocks.append(((6, 1, 3), idx["veilbound:ember_brazier"], None))
    blocks.append(((6, 1, 9), idx["veilbound:ember_brazier"], None))
    for x in (4, 8):
        blocks.append(((x, 1, 6), idx["veilbound:voidstone_bricks"], None))
    chest_nbt = {"id": (TAG_STRING, "minecraft:chest"), "LootTable": (TAG_STRING, "veilbound:chests/veil_ruin")}
    blocks.append(((6, 1, 6), idx["minecraft:chest"], chest_nbt))
    structure_nbt(f"{out}/structure/veil_ruin.nbt", (W, 10, W), palette, blocks)
    # worldgen structure (jigsaw, single piece)
    wg = os.path.join(out, "worldgen")
    jwrite(f"{wg}/structure/veil_ruin.json", {
        "type": "minecraft:jigsaw",
        "biomes": "#veilbound:has_structure/veil_ruin",
        "step": "surface_structures",
        "spawn_overrides": {},
        "start_pool": "veilbound:veil_ruin_pool",
        "size": 1,
        "start_height": {"absolute": 0},
        "max_distance_from_center": 32,
        "project_start_to_heightmap": "WORLD_SURFACE_WG",
        "use_expansion_hack": False,
        "terrain_adaptation": "beard_thin",
    })
    jwrite(f"{wg}/template_pool/veil_ruin_pool.json", {
        "name": "veilbound:veil_ruin_pool",
        "fallback": "minecraft:empty",
        "elements": [{
            "weight": 1,
            "element": {
                "element_type": "minecraft:legacy_single_pool_element",
                "location": "veilbound:veil_ruin",
                "processors": "minecraft:empty",
                "projection": "rigid",
            },
        }],
    })
    jwrite(f"{wg}/structure_set/veil_ruins.json", {
        "structures": [{"structure": "veilbound:veil_ruin", "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "salt": 90210, "spacing": 28, "separation": 10,
                      "spread_type": "linear"},
    })
    # chest loot
    jwrite(f"{out}/loot_table/chests/veil_ruin.json", {
        "type": "minecraft:chest",
        "pools": [
            {"bonus_rolls": 0.0, "rolls": 1.0, "entries": [{"type": "minecraft:item", "name": "veilbound:glimmer_shard",
                "functions": [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": 2.0, "max": 5.0}, "add": False}]}]},
            {"bonus_rolls": 0.0, "rolls": 1.0, "conditions": [{"condition": "minecraft:random_chance", "chance": 0.6}],
             "entries": [{"type": "minecraft:item", "name": "veilbound:aetherite_ingot"}]},
            {"bonus_rolls": 0.0, "rolls": 1.0, "conditions": [{"condition": "minecraft:random_chance", "chance": 0.4}],
             "entries": [{"type": "minecraft:item", "name": "veilbound:veil_essence",
                "functions": [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": 1.0, "max": 3.0}, "add": False}]}]},
            {"bonus_rolls": 0.0, "rolls": 1.0, "conditions": [{"condition": "minecraft:random_chance", "chance": 0.25}],
             "entries": [{"type": "minecraft:item", "name": "veilbound:ember_core"}]},
        ],
    })


def gen_depths_dimension():
    jwrite(f"{DATA}/veilbound/dimension_type/the_depths.json", {
        "ambient_light": 0.0, "bed_works": False, "coordinate_scale": 1.0, "effects": "minecraft:the_nether",
        "fixed_time": 18000, "has_ceiling": True, "has_raids": True, "has_skylight": False,
        "height": 384, "infiniburn": "#minecraft:infiniburn_nether", "logical_height": 384,
        "min_y": -64, "monster_spawn_block_light_limit": 0,
        "monster_spawn_light_level": {"type": "minecraft:uniform", "min_inclusive": 0, "max_inclusive": 7},
        "natural": False, "piglin_safe": False, "respawn_anchor_works": False, "ultrawarm": False})
    jwrite(f"{DATA}/veilbound/dimension/the_depths.json", {
        "type": "veilbound:the_depths",
        "generator": {
            "type": "minecraft:noise",
            "biome_source": {"type": "minecraft:multi_noise", "biomes": [
                {"parameters": {"temperature": 0.0, "humidity": 0.0, "continentalness": 0.0, "erosion": 0.0, "depth": 0.0, "weirdness": 0.0, "offset": 0.0}, "biome": "veilbound:abyss_wastes"}]},
            "settings": "veilbound:the_depths"}})
    ref = os.path.join(os.path.dirname(os.path.abspath(__file__)), "templates", "floating_islands.json")
    ns = json.load(open(ref))
    ns["default_block"] = {"Name": "veilbound:abyss_stone"}
    ns["default_fluid"] = {"Name": "minecraft:lava", "Properties": {"level": "0"}}
    ns["noise"] = {"min_y": -64, "height": 384, "size_horizontal": 2, "size_vertical": 1}
    ns["sea_level"] = -64
    ns["surface_rule"] = {"type": "minecraft:block", "result_state": {"Name": "veilbound:abyss_stone"}}
    grads = []
    def walk(o):
        if isinstance(o, dict):
            if o.get("type") == "minecraft:y_clamped_gradient":
                grads.append(o)
            for v in o.values():
                walk(v)
        elif isinstance(o, list):
            for v in o:
                walk(v)
    walk(ns["noise_router"])
    if len(grads) >= 2:
        grads[0]["from_y"], grads[0]["to_y"] = -32, 40
        grads[1]["from_y"], grads[1]["to_y"] = 80, 176
    jwrite(f"{DATA}/veilbound/worldgen/noise_settings/the_depths.json", ns)
    wg = os.path.join(DATA, "veilbound", "worldgen")
    def spawn(t, w, mn, mx):
        return {"type": f"veilbound:{t}", "weight": w, "minCount": mn, "maxCount": mx}
    jwrite(f"{wg}/biome/abyss_wastes.json", {
        "carvers": {}, "downfall": 0.0, "has_precipitation": False, "temperature": 0.8,
        "effects": {"fog_color": 0x1A0505, "sky_color": 0x0D0202, "water_color": 0xFF4400, "water_fog_color": 0x992200,
                    "mood_sound": {"block_search_extent": 8, "offset": 2.0, "sound": "minecraft:ambient.nether_wastes.mood", "tick_delay": 6000}},
        "spawn_costs": {},
        "spawners": {"ambient": [spawn("veil_moth", 6, 1, 2)], "axolotls": [], "creature": [spawn("shardling", 6, 1, 2)],
                     "misc": [], "monster": [spawn("hollow_husk", 35, 1, 3), spawn("veil_reaver", 25, 1, 2),
                                              spawn("ember_wisp", 20, 1, 3), spawn("crystal_golem", 5, 1, 1)],
                     "underground_water_creature": [], "water_ambient": [], "water_creature": []},
        "features": [[], [], [], [], [], [], ["veilbound:ore_voidstone", "veilbound:ore_glimmer_shard"], ["veilbound:crystal_cluster"], [], [], []],
    })


def main():
    import shutil
    for d in (ASSETS, os.path.join(DATA, "veilbound"), os.path.join(DATA, "minecraft", "tags", "block")):
        shutil.rmtree(d, ignore_errors=True)
    gen_textures()
    gen_models()
    gen_misc_assets()
    gen_block_loot()
    gen_entity_loot()
    gen_recipes()
    gen_tags()
    gen_advancements()
    gen_worldgen()
    gen_structures()
    gen_depths_dimension()
    print("resources generated under", ROOT)


if __name__ == "__main__":
    main()
