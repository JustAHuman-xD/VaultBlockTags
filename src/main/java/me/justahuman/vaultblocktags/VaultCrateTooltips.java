package me.justahuman.vaultblocktags;

import com.mojang.datafixers.util.Either;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.quark.content.client.tooltip.ShulkerBoxTooltips;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class VaultCrateTooltips {

    @OnlyIn(Dist.CLIENT)
    public static void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VaultCrateBlock && Screen.hasShiftDown()) {
            NbtCompound pTag = itemStack.getSubNbt("BlockEntityTag");
            if (pTag != null && pTag.contains("Items", NbtCompound.LIST_TYPE)) {
                DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(pTag.getList("Items", NbtCompound.COMPOUND_TYPE).size(), ItemStack.EMPTY);
                Inventories.readNbt(pTag, itemStacks);
                event.getTooltipElements().add(1, Either.right(new CrateComponent(itemStacks)));
            } else if (pTag != null && pTag.contains("items", NbtCompound.LIST_TYPE)) {
                List<OverSizedItemStack> items = new ArrayList<>();
                NBTHelper.readCollection(pTag, "items", NbtCompound.class, OverSizedItemStack::deserialize, items);
                DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(items.size(), ItemStack.EMPTY);
                for (int i = 0; i < items.size(); i++) {
                    OverSizedItemStack overSized = items.get(i);
                    itemStacks.set(i, overSized.overSizedStack());
                }
                event.getTooltipElements().add(1, Either.right(new CrateComponent(itemStacks)));
            }
        }
    }

    public record CrateComponent(DefaultedList<ItemStack> itemStacks, int width, int height) implements TooltipData, TooltipComponent {

        public CrateComponent(DefaultedList<ItemStack> itemStacks) {
            this(
                    itemStacks,
                    Math.min(itemStacks.size(), 9),
                    (itemStacks.size() / 9) + (itemStacks.size() % 9 != 0 ? 1 : 0)
            );
        }

        @Override
        @ParametersAreNonnullByDefault
        public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            int currentX = x;
            int currentY = y - 1;
            int textWidth = 10 + 18 * 9;

            int right = currentX + textWidth;
            Window window = minecraft.getWindow();
            if (right > window.getWidth()) {
                currentX -= right - window.getScaledWidth();
            }

            matrices.push();
            matrices.translate(0, 0, 700);

            ShulkerBoxTooltips.ShulkerComponent.renderTooltipBackground(minecraft, matrices, currentX, currentY, width, height, -1);

            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack itemStack = itemStacks.get(i);
                if (!itemStack.isEmpty()) {
                    int xp = currentX + 6 + i % 9 * 18;
                    int yp = currentY + 6 + i / 9 * 18;
                    itemRenderer.renderInGui(itemStack, xp, yp);
                    itemRenderer.renderGuiItemOverlay(minecraft.textRenderer, itemStack, xp, yp);
                }
            }
        }

        @Override
        public int getHeight() {
            return 11 + this.height * 18;
        }

        @Override
        public int getWidth(@Nonnull TextRenderer font) {
            return 9 + this.width * 18;
        }
    }
}
