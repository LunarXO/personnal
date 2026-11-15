package com.realmwalker.gui;

import java.util.List;

import com.realmwalker.ModDimensions;
import com.realmwalker.Realmwalker;

import net.minecraft.resources.ResourceLocation;

/** Static definition of the Codex pages. Pages unlock when the required advancement is earned. */
public final class CodexPages {
    public record Page(String titleKey, String bodyKey, ResourceLocation requiredAdvancement) {
    }

    private static final String[] DIMS = { "lumenfen", "saltglass_expanse", "cogspire", "frostmarrow", "stormspire" };

    public static List<Page> pages() {
        java.util.ArrayList<Page> pages = new java.util.ArrayList<>();
        pages.add(new Page("codex.realmwalker.intro.title", "codex.realmwalker.intro.body", null));
        for (String dim : DIMS) {
            ResourceLocation unlock = Realmwalker.id(dim + "_entered");
            pages.add(new Page("codex.realmwalker." + dim + ".lore.title", "codex.realmwalker." + dim + ".lore.body", unlock));
            pages.add(new Page("codex.realmwalker." + dim + ".objectives.title", "codex.realmwalker." + dim + ".objectives.body", unlock));
            pages.add(new Page("codex.realmwalker." + dim + ".hints.title", "codex.realmwalker." + dim + ".hints.body", unlock));
        }
        pages.add(new Page("codex.realmwalker.final.title", "codex.realmwalker.final.body", Realmwalker.id("ascension")));
        return pages;
    }

    private CodexPages() {
    }
}
