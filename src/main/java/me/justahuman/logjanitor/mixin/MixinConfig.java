package me.justahuman.logjanitor.mixin;

import com.google.common.collect.ImmutableMap;
import me.justahuman.logjanitor.LogJanitor;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin {
    private static final Map<String, List<String>> MIXINS = new HashMap<>();
    private static final String PATH_PREFIX = "me.justahuman.logjanitor.mixin";

    @Override
    public void onLoad(String mixinPackage) {
        LogJanitor.loadConfig();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] pieces = mixinClassName.substring(PATH_PREFIX.length()).split("\\.");
        String type = pieces[0];
        String mixin = pieces[1];

        List<String> mixins = MIXINS.getOrDefault(type, new ArrayList<>());
        mixins.add(mixin);
        mixins.sort(Comparator.naturalOrder());
        MIXINS.put(type, mixins);

        return LogJanitor.isMixinEnabled(type, mixin);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    public static Map<String, List<String>> getSortedMixins() {
        return ImmutableMap.copyOf(MIXINS);
    }
}
