package me.justahuman.vaultblocktags.mixin;

import iskallia.vault.core.card.CardEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CardEntry.Color.class)
public interface CardColorAccessor {
    @Accessor(remap = false)
    int getColor();
}
