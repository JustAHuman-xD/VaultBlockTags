package me.justahuman.vaultblocktags.mixin.forge;

import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameData.class)
public class GameDataMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"), method = "checkPrefix", remap = false)
    private static void checkPrefix(Logger instance, String s, Object o1, Object o2, Object o3) {
        // We don't want to log anymore
    }
}
