package me.justahuman.vaultblocktags.mixin.quark;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.base.module.ModuleFinder;

@Mixin(ModuleFinder.class)
public class FinderMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), method = "loadModule", remap = false)
    public void loadModule(Logger instance, String s) {
        // We don't want to log anymore
    }
}
