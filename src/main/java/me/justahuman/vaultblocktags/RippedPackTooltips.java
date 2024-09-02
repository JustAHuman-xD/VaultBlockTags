package me.justahuman.vaultblocktags;

import com.mojang.datafixers.util.Either;
import iskallia.vault.core.card.Card;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.item.CardItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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

    public record PackOutcomesComponent(List<ItemStack> outcomes) implements TooltipData, TooltipComponent {

        @Override
        @ParametersAreNonnullByDefault
        public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer renderer, int pTicks) {
            int currentX = x;
            int currentY = y;
            for (ItemStack outcome : outcomes) {
                Card card = CardItem.getCard(outcome);

            }
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth(@Nonnull TextRenderer textRenderer) {
            return 0;
        }
    }
}
