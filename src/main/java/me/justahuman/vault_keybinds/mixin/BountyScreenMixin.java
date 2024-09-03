package me.justahuman.vault_keybinds.mixin;

import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.bounty.element.BountyTableContainerElement;
import me.justahuman.vault_keybinds.VaultKeybinds;
import me.justahuman.vault_keybinds.api.RerollButtonHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BountyScreen.class)
public class BountyScreenMixin {
    @Shadow @Final private BountyTableContainerElement bountyTableContainerElement;

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
        if (bountyTableContainerElement instanceof RerollButtonHolder holder && pKeyCode == VaultKeybinds.REROLL_BOUNTY_KEYBIND.getKey().getCode()) {
            ButtonElement<?> rerollButton = holder.vault_keybinds$getRerollButton();
            if (rerollButton != null && !rerollButton.isDisabled()) {
                ((BountyContainerAccessor) bountyTableContainerElement).invokeHandleReroll();
                cir.setReturnValue(true);
            }
        }
    }
}
