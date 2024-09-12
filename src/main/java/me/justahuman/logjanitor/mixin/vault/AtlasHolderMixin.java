package me.justahuman.logjanitor.mixin.vault;

import iskallia.vault.client.atlas.AbstractTextureAtlasHolder;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractTextureAtlasHolder.class)
public class AtlasHolderMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"), method = "validateTextures", remap = false)
    public void validateTextures(Logger instance, String s) {
        // We don't want to log anymore
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"), method = "lambda$validateTextures$0", remap = false)
    private static void validateTexturesLambda(Logger instance, String s) {
        // We don't want to log anymore
    }
}
