package me.justahuman.logjanitor.mixin.jei;

import mezz.jei.common.load.PluginCaller;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PluginCaller.class)
public class PluginCallerMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), method = "callOnPlugins", remap = false)
    private static void callOnPlugins(Logger instance, String s, Object o1, Object o2) {
        // We don't want to log anymore
    }
}
