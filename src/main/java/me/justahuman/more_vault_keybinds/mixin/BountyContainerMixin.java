package me.justahuman.more_vault_keybinds.mixin;

import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.bounty.element.BountyTableContainerElement;
import me.justahuman.more_vault_keybinds.api.RerollButtonHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BountyTableContainerElement.class)
public abstract class BountyContainerMixin extends ContainerElement<BountyContainerMixin> implements RerollButtonHolder {
    @Unique private ButtonElement<?> more_vault_keybinds$rerollButton;

    private BountyContainerMixin(ISpatial spatial) {
        super(spatial);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/screen/bounty/element/BountyTableContainerElement;addElement(Liskallia/vault/client/gui/framework/element/spi/IElement;)Liskallia/vault/client/gui/framework/element/spi/IElement;"), method = "createRerollButton")
    public IElement addRerollButton(BountyTableContainerElement instance, IElement iElement) {
        if (iElement instanceof ButtonElement<?> button) {
            this.more_vault_keybinds$rerollButton = this.addElement(button);
            return this.more_vault_keybinds$rerollButton;
        }
        return this.addElement(iElement);
    }

    @Override
    public ButtonElement<?> more_vault_keybinds$getRerollButton() {
        return this.more_vault_keybinds$rerollButton;
    }
}
