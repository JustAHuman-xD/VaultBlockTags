package me.justahuman.vaultblocktags.mixin.jei;

import me.justahuman.vaultblocktags.api.OverlayExtension;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.common.bookmarks.BookmarkList;
import mezz.jei.common.gui.GuiScreenHelper;
import mezz.jei.common.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.common.ingredients.RegisteredIngredients;
import mezz.jei.common.startup.OverlayHelper;
import mezz.jei.common.startup.StartData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverlayHelper.class)
public class OverlayHelperMixin {
    @Inject(at = @At("RETURN"), method = "createBookmarkOverlay", remap = false)
    private static void createBookmarkOverlay(StartData data, RegisteredIngredients registeredIngredients, GuiScreenHelper guiScreenHelper, BookmarkList bookmarkList, IModIdHelper modIdHelper, CallbackInfoReturnable<BookmarkOverlay> cir) {
        BookmarkOverlay overlay = cir.getReturnValue();
        if (overlay instanceof OverlayExtension holder) {
            holder.vaultblocktags$createManager(registeredIngredients);
        }
    }
}
