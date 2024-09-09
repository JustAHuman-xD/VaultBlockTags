package me.justahuman.vaultblocktags;

import com.mojang.datafixers.util.Either;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardCondition;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.CardModifier;
import iskallia.vault.core.card.CardScaler;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.item.CardItem;
import me.justahuman.vaultblocktags.mixin.CardEntryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraftforge.client.event.RenderTooltipEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.util.Formatting.*;

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
                int tier = card.getTier();
                List<Text> lines = new ArrayList<>();

                MutableText newLine = new LiteralText(WHITE + "Tier: ");
                newLine = newLine.append(new LiteralText(YELLOW + String.valueOf(tier)));
                lines.add(newLine);

                newLine = new LiteralText(WHITE + "Color: ");
                for (Iterator<CardEntry.Color> iterator = card.getColors().iterator(); iterator.hasNext(); ) {
                    CardEntry.Color color = iterator.next();
                    newLine = newLine.append(color.getColoredText());
                    if (iterator.hasNext()) {
                        newLine = newLine.append(GRAY + ",");
                    }

                    if (textRenderer.getWidth(newLine) >= 40) {
                        lines.add(newLine);
                        newLine = new LiteralText("");
                    }
                }

                if (!newLine.toString().isBlank()) {
                    lines.add(newLine);
                }

                List<String> groups = new ArrayList<>(card.getGroups());
                List<String> types = new ArrayList<>();
                groups.stream().filter(Card.TYPES::contains).forEach(group -> {
                    groups.remove(group);
                    types.add(group);
                });

                if (!types.isEmpty()) {
                    newLine = new LiteralText(WHITE + "Type: ");
                    newLine = appendValues(textRenderer, lines, newLine, types);
                }

                if (!newLine.toString().isBlank()) {
                    lines.add(newLine);
                }

                if (!groups.isEmpty()) {
                    newLine = new LiteralText(WHITE + "Groups: ");
                    newLine = appendValues(textRenderer, lines, newLine, groups);
                }

                if (!newLine.toString().isBlank()) {
                    lines.add(newLine);
                }

                for (CardEntry entry : card.getEntries()) {
                    CardModifier<?> modifier = entry.getModifier();
                    CardScaler scaler = entry.getScaler();
                    CardCondition condition = ((CardEntryAccessor) entry).getCondition();
                    List<Text> dummy = new ArrayList<>();

                    modifier.addText(dummy, 0, context, 0, tier);
                    lines.addAll(dummy);
                    dummy.clear();

                    scaler.addText(dummy, 0, context, 0, tier);
                    lines.addAll(segmentText(textRenderer, dummy.toArray(Text[]::new)));
                    dummy.clear();

                    condition.addText(dummy, 0, context, 0, tier);
                    lines.addAll(segmentText(textRenderer, dummy.toArray(Text[]::new)));
                    dummy.clear();
                }

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

        private MutableText appendValues(TextRenderer textRenderer, List<Text> lines, MutableText newLine, List<String> values) {
            for (int i = 0; i < values.size(); i++) {
                String type = values.get(i);
                newLine = newLine.append(new LiteralText(type).styled(style -> style.withColor(13421772)));
                if (i < values.size() - 1) {
                    newLine = newLine.append(GRAY + ",");
                }

                if (textRenderer.getWidth(newLine) >= 40) {
                    lines.add(newLine);
                    newLine = new LiteralText("");
                }
            }
            return newLine;
        }

        private @Nonnull List<Text> segmentText(TextRenderer textRenderer, Text[] array) {
            List<Text> split = new ArrayList<>();
            for (Text line : array) {
                if (textRenderer.getWidth(line) <= 40) {
                    split.add(line);
                    continue;
                }

                String text = line.asString();
                String[] pieces = text.split(" ");
                StringBuilder newLine = new StringBuilder();
                for (String piece : pieces) {
                    newLine.append(piece);
                    if (newLine.length() >= 40) {
                        split.add(new LiteralText(newLine.toString()));
                        newLine = new StringBuilder();
                    } else {
                        newLine.append(" ");
                    }
                }

                if (!newLine.isEmpty()) {
                    split.add(new LiteralText(newLine.toString()));
                }
            }
            return split;
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
