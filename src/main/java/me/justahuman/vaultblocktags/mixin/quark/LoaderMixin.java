package me.justahuman.vaultblocktags.mixin.quark;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.base.module.ModuleLoader;

@Mixin(ModuleLoader.class)
public class LoaderMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), method = "dispatch", remap = false)
    public void dispatch(Logger instance, String s) {
        // We don't want to log anymore
    }
}
