package me.justahuman.vaultblocktags.mixin.patchouli;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

@Mixin(PageDoubleRecipeRegistry.class)
public class DoubleRecipeMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), method = "loadRecipe*", remap = false)
    public void loadRecipe(Logger instance, String s, Object o1, Object o2) {
        // We don't want to log anymore
    }
}
