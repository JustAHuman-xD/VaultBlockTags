package me.justahuman.vaultblocktags.mixin;

import iskallia.vault.container.inventory.CardDeckContainerMenu;
import me.justahuman.vaultblocktags.VaultBlockTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CardDeckContainerMenu.class)
public abstract class CardDeckContainerMenuMixin extends ScreenHandler {
    protected CardDeckContainerMenuMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        VaultBlockTags.DECK_MODIFIER_CACHE.remove(player.getUuid());
    }
}
