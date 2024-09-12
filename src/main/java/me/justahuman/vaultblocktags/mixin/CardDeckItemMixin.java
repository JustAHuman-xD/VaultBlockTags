package me.justahuman.vaultblocktags.mixin;

import com.google.common.collect.Multimap;
import iskallia.vault.item.CardDeckItem;
import me.justahuman.vaultblocktags.VaultBlockTags;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

@Mixin(CardDeckItem.class)
public class CardDeckItemMixin {
    @Inject(at = @At("HEAD"), method = "getAttributeModifiers", cancellable = true, remap = false)
    public void getModifiers(SlotContext context, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        var modifiers = VaultBlockTags.DECK_MODIFIER_CACHE.get(context.entity().getUuid());
        if (modifiers != null) {
            cir.setReturnValue(modifiers);
        }
    }

    @Inject(at = @At("RETURN"), method = "getAttributeModifiers", remap = false)
    public void setModifiers(SlotContext context, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        UUID entityUuid = context.entity().getUuid();
        var modifiers = VaultBlockTags.DECK_MODIFIER_CACHE.get(entityUuid);
        if (modifiers == null) {
            VaultBlockTags.DECK_MODIFIER_CACHE.put(entityUuid, cir.getReturnValue());
        }
    }
}
