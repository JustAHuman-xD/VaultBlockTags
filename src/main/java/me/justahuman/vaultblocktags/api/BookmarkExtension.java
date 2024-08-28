package me.justahuman.vaultblocktags.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public interface BookmarkExtension {
    void vaultblocktags$renderForeground(Minecraft minecraft, PoseStack poseStack, AbstractContainerScreen<?> screen, int mouseX, int mouseY);
}
