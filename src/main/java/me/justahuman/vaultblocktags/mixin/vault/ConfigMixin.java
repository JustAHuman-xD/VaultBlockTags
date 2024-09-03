package me.justahuman.vaultblocktags.mixin.vault;

import iskallia.vault.config.Config;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Config.class)
public class ConfigMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), method = "readConfig", remap = false)
    public void readConfig(Logger instance, String s) {
        // We don't want to log anymore
    }
}
