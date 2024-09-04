package me.justahuman.vaultblocktags;

import com.mojang.datafixers.util.Either;
import iskallia.vault.core.card.Card;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.item.CardItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraftforge.client.event.RenderTooltipEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class RippedPackTooltips {
    public static void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof BoosterPackItem) {
            List<ItemStack> outcomes = BoosterPackItem.getOutcomes(itemStack);
            if (outcomes != null && !outcomes.isEmpty() && Screen.hasShiftDown()) {
                event.getTooltipElements().add(1, Either.right(new PackOutcomesComponent(outcomes)));
            }
        }
    }

    public static class PackOutcomesComponent implements TooltipData, TooltipComponent {
        protected final List<ItemStack> outcomes;
        protected int height = 0;
        protected int width = 0;

        public PackOutcomesComponent(List<ItemStack> outcomes) {
            this.outcomes = outcomes;
        }

        @Override
        @ParametersAreNonnullByDefault
        public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer renderer, int pTicks) {
            int currentX = x;
            int currentY = y + 18;
            MinecraftClient client = MinecraftClient.getInstance();
            TooltipContext context = client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL;

            for (ItemStack outcome : outcomes) {
                renderer.renderGuiItemIcon(outcome, currentX + 6, y + 6);

                int xIncrease = 18;
                int yDecrease = 0;
                Card card = CardItem.getCard(outcome);
                List<Text> lines = new ArrayList<>();
                card.addText(lines, 0, context, 0);
                for (Text line : lines) {
                    xIncrease = Math.max(xIncrease, textRenderer.getWidth(line) + 2);
                    textRenderer.draw(matrices, line, currentX, currentY, 16777215);
                    currentY += textRenderer.fontHeight + 2;
                    yDecrease += textRenderer.fontHeight + 2;
                }

                this.width += xIncrease;
                this.height = Math.max(this.height, yDecrease);
                currentX += xIncrease;
                currentY -= yDecrease;
            }

            if (this.width != 0 && this.height != 0) {
                this.width += 4;
                this.height += 18;
            }
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getWidth(@Nonnull TextRenderer textRenderer) {
            return this.width;
        }
    }
}
