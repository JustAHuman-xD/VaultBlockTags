package me.justahuman.vaultblocktags.mixin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.vaultblocktags.api.BookmarkExtension;
import mezz.jei.Internal;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.core.config.IClientConfig;
import mezz.jei.core.config.IWorldConfig;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.textures.Textures;
import mezz.jei.input.mouse.IUserInputHandler;
import mezz.jei.input.mouse.handlers.CheatInputHandler;
import mezz.jei.input.mouse.handlers.CombinedInputHandler;
import mezz.jei.input.mouse.handlers.ProxyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookmarkOverlay.class)
public abstract class BookmarkOverlayMixin implements BookmarkExtension {
    @Unique private GhostIngredientDragManager vaultblocktags$ghostIngredientDragManager;

    @Shadow(remap = false) @Final private IngredientGridWithNavigation contents;
    @Shadow(remap = false) @Final private IWorldConfig worldConfig;
    @Shadow(remap = false) @Final private GuiIconToggleButton bookmarkButton;
    @Shadow(remap = false) @Final private CheatInputHandler cheatInputHandler;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(BookmarkList bookmarkList, Textures textures, IngredientGridWithNavigation contents, IClientConfig clientConfig, IWorldConfig worldConfig, GuiScreenHelper guiScreenHelper, IConnectionToServer serverConnection, CallbackInfo ci) {
        this.vaultblocktags$ghostIngredientDragManager = new GhostIngredientDragManager(this.contents, guiScreenHelper, Internal.getRegisteredIngredients(), this.worldConfig);
    }

    @Override
    public void vaultblocktags$renderForeground(Minecraft minecraft, PoseStack poseStack, AbstractContainerScreen<?> gui, int mouseX, int mouseY) {
        if (this.isListDisplayed()) {
            poseStack.pushPose();
            poseStack.translate(-gui.getGuiLeft(), -gui.getGuiTop(), 0.0);
            this.vaultblocktags$ghostIngredientDragManager.drawOnForeground(minecraft, poseStack, mouseX, mouseY);
            poseStack.popPose();
        }
    }

    @Inject(at = @At("TAIL"), method = "drawTooltips", remap = false)
    public void drawTooltips(Minecraft minecraft, PoseStack poseStack, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.isListDisplayed()) {
            this.vaultblocktags$ghostIngredientDragManager.drawTooltips(minecraft, poseStack, mouseX, mouseY);
        }
    }

    @Inject(at = @At("HEAD"), method = "createInputHandler", remap = false, cancellable = true)
    public void createInputHandler(CallbackInfoReturnable<IUserInputHandler> cir) {
        IUserInputHandler bookmarkButtonInputHandler = this.bookmarkButton.createInputHandler();
        IUserInputHandler displayedInputHandler = new CombinedInputHandler(this.vaultblocktags$ghostIngredientDragManager.createInputHandler(), this.cheatInputHandler, this.contents.createInputHandler(), bookmarkButtonInputHandler);
        cir.setReturnValue(new ProxyInputHandler(() -> this.isListDisplayed() ? displayedInputHandler : bookmarkButtonInputHandler));
    }

    @Shadow(remap = false) public abstract boolean isListDisplayed();
}
