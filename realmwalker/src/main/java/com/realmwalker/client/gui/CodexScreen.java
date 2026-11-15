package com.realmwalker.client.gui;

import java.util.List;

import com.realmwalker.gui.CodexMenu;
import com.realmwalker.gui.CodexPages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CodexScreen extends Screen {
    private final List<CodexPages.Page> unlocked;
    private int index = 0;
    private Button prevButton;
    private Button nextButton;

    public CodexScreen(CodexMenu menu, Inventory inventory, Component title) {
        super(title);
        this.unlocked = CodexPages.pages().stream().filter(this::isUnlocked).toList();
    }

    private boolean isUnlocked(CodexPages.Page page) {
        if (page.requiredAdvancement() == null || Minecraft.getInstance().player == null) {
            return page.requiredAdvancement() == null;
        }
        var holder = Minecraft.getInstance().player.connection.getAdvancements().getAdvancement(page.requiredAdvancement());
        return holder != null && Minecraft.getInstance().player.connection.getAdvancements().getProgress(holder).isDone();
    }

    @Override
    protected void init() {
        int y = this.height - 40;
        this.prevButton = this.addRenderableWidget(Button.builder(Component.translatable("codex.realmwalker.prev"), b -> setPage(this.index - 1))
                .bounds(this.width / 2 - 100, y, 95, 20).build());
        this.nextButton = this.addRenderableWidget(Button.builder(Component.translatable("codex.realmwalker.next"), b -> setPage(this.index + 1))
                .bounds(this.width / 2 + 5, y, 95, 20).build());
        updateButtons();
    }

    private void setPage(int page) {
        if (page >= 0 && page < this.unlocked.size()) {
            this.index = page;
        }
        updateButtons();
    }

    private void updateButtons() {
        this.prevButton.active = this.index > 0;
        this.nextButton.active = this.index < this.unlocked.size() - 1;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (this.unlocked.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable("codex.realmwalker.empty"), this.width / 2, this.height / 2, 0xC77DFF);
            return;
        }
        CodexPages.Page page = this.unlocked.get(this.index);
        graphics.drawCenteredString(this.font, Component.translatable(page.titleKey()), this.width / 2, 40, 0xFFD700);
        Component body = Component.translatable(page.bodyKey());
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(body, this.width - 80);
        int y = 70;
        for (var line : lines) {
            graphics.drawString(this.font, line, (this.width - this.font.width(line)) / 2, y, 0xE8E0D0, false);
            y += 10;
            if (y > this.height - 60) {
                break;
            }
        }
        graphics.drawCenteredString(this.font, (this.index + 1) + " / " + this.unlocked.size(), this.width / 2, this.height - 55, 0x9A9A9A);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
