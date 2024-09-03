package me.justahuman.more_vault_keybinds.mixin;

import iskallia.vault.client.gui.screen.bounty.element.BountyTableContainerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BountyTableContainerElement.class)
public interface BountyContainerAccessor {
    @Invoker(remap = false)
    void invokeHandleReroll();
}
