package me.justahuman.logjanitor.mixin.everycomp;

import net.mehvahdjukaar.every_compat.modules.mcaw.MacawDoorsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MacawDoorsModule.class)
public class MDoorsModuleMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "block/oak_japanese2_door_lower"), remap = false)
    public String doorLower(String constant) {
        return "block/japanese_oak_lower";
    }

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "block/oak_japanese2_door_upper"), remap = false)
    public String doorUpper(String constant) {
        return "block/japanese_oak_upper";
    }

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "item/oak_japanese2_door"), remap = false)
    public String door(String constant) {
        return "item/oak_japanese_door";
    }
}
