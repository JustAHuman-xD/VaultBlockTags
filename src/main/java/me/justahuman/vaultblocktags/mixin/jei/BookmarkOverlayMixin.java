package me.justahuman.vaultblocktags.mixin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.vaultblocktags.api.OverlayExtension;
import mezz.jei.common.bookmarks.BookmarkList;
import mezz.jei.common.gui.GuiScreenHelper;
import mezz.jei.common.gui.elements.GuiIconToggleButton;
import mezz.jei.common.gui.ghost.GhostIngredientDragManager;
import mezz.jei.common.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.common.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.ingredients.RegisteredIngredients;
import mezz.jei.common.input.IKeyBindings;
import mezz.jei.common.input.IUserInputHandler;
import mezz.jei.common.input.handlers.CheatInputHandler;
import mezz.jei.common.input.handlers.CombinedInputHandler;
import mezz.jei.common.input.handlers.ProxyInputHandler;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.core.config.IClientConfig;
import mezz.jei.core.config.IWorldConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookmarkOverlay.class)
public abstract class BookmarkOverlayMixin implements OverlayExtension {
    @Unique private GhostIngredientDragManager vaultblocktags$ghostIngredientDragManager;
    @Unique private GuiScreenHelper vaultblocktags$guiScreenHelper;

    @Shadow(remap = false) @Final private IngredientGridWithNavigation contents;
    @Shadow(remap = false) @Final private IWorldConfig worldConfig;
    @Shadow(remap = false) @Final private GuiIconToggleButton bookmarkButton;
    @Shadow(remap = false) @Final private CheatInputHandler cheatInputHandler;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(BookmarkList bookmarkList, Textures textures, IngredientGridWithNavigation contents, IClientConfig clientConfig, IWorldConfig worldConfig, GuiScreenHelper guiScreenHelper, IConnectionToServer serverConnection, IKeyBindings keyBindings, CallbackInfo ci) {
        this.vaultblocktags$guiScreenHelper = guiScreenHelper;
    }

    @Inject(at = @At("TAIL"), method = "drawScreen", remap = false)
    public void drawScreen(Minecraft minecraft, PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (this.isListDisplayed()) {
            this.vaultblocktags$ghostIngredientDragManager.drawOnForeground(minecraft, poseStack, mouseX, mouseY);
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

    @Override
    public void vaultblocktags$createManager(RegisteredIngredients registeredIngredients) {
        this.vaultblocktags$ghostIngredientDragManager = new GhostIngredientDragManager(this.contents, this.vaultblocktags$guiScreenHelper, registeredIngredients, this.worldConfig);
    }
}
