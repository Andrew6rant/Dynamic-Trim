package io.github.andrew6rant.dynamictrim.mixin;

import io.github.andrew6rant.dynamictrim.DynamicArmorModelGenerator;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public class ItemTextureMixin {
    @Inject(method = "getItemLayer", at = @At(value = "HEAD"), cancellable = true)
    private static void add_forgingBlankLayer(ItemStack stack, boolean direct, CallbackInfoReturnable<RenderLayer> cir) {
        if (stack.getItem() instanceof ArmorItem) {
            //System.out.println("ArmorItem_getItemLayer: "+stack.getItem());
            cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(DynamicArmorModelGenerator.TEXTURE_CACHE.get(stack.getOrCreateNbt())));
        }
    }
}
