package io.github.andrew6rant.dynamictrim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicTrimClient implements ClientModInitializer {
    public static final Boolean isStackedTrimsEnabled = FabricLoader.getInstance().isModLoaded("stacked_trims");

    @Override
    public void onInitializeClient() {
        //ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new ArmorModelProvider());
        /*
        ModelPredicateProviderRegistry.register(Items.IRON_CHESTPLATE, new Identifier("trim_type"), (itemStack, clientWorld, livingEntity, j) -> {
            assert clientWorld != null;
            return ArmorTrim.getTrim(clientWorld.getRegistryManager(), itemStack).map(ArmorTrim::getMaterial).map(RegistryEntry::value).map(ArmorTrimMaterial::itemModelIndex).orElse(0.0F);
        });*/
    }
}