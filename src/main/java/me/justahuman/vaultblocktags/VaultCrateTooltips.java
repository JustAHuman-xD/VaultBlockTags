package me.justahuman.vaultblocktags;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import iskallia.vault.block.VaultCrateBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.quark.content.client.tooltip.ShulkerBoxTooltips;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class VaultCrateTooltips {

    @OnlyIn(Dist.CLIENT)
    public static void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VaultCrateBlock && Screen.hasShiftDown()) {
            CompoundTag pTag = itemStack.getTagElement("BlockEntityTag");
            if (pTag != null && pTag.contains("Items", 9)) {
                NonNullList<ItemStack> itemStacks = NonNullList.withSize(pTag.getList("Items", 10).size(), ItemStack.EMPTY);
                ContainerHelper.loadAllItems(pTag, itemStacks);
                event.getTooltipElements().add(1, Either.right(new CrateComponent(itemStacks)));
            }
        }
    }

    public record CrateComponent(NonNullList<ItemStack> itemStacks) implements ClientTooltipComponent, TooltipComponent {

        @Override
        @ParametersAreNonnullByDefault
        public void renderImage(Font font, int tooltipX, int tooltipY, PoseStack pose, ItemRenderer renderer, int pTicks) {
            Minecraft minecraft = Minecraft.getInstance();
            int currentX = tooltipX;
            int currentY = tooltipY - 1;
            int textWidth = 10 + 18 * 9;

            int right = currentX + textWidth;
            Window window = minecraft.getWindow();
            if (right > window.getScreenWidth()) {
                currentX -= right - window.getGuiScaledWidth();
            }

            pose.pushPose();
            pose.translate(0, 0, 700);

            ShulkerBoxTooltips.ShulkerComponent.renderTooltipBackground(minecraft, pose, currentX, currentY, 9, 6, -1);

            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack itemStack = itemStacks.get(i);
                if (!itemStack.isEmpty()) {
                    int xp = currentX + 6 + i % 9 * 18;
                    int yp = currentY + 6 + i / 9 * 18;
                    renderer.renderAndDecorateItem(itemStack, xp, yp);
                    renderer.renderGuiItemDecorations(minecraft.font, itemStack, xp, yp);
                }
            }
        }

        @Override
        public int getHeight() {
            return 119;
        }

        @Override
        public int getWidth(@Nonnull Font font) {
            return 171;
        }
    }
}
