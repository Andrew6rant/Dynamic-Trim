package io.github.andrew6rant.dynamictrim.mixin;

import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public class ItemModelsMixin {
    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;", at = @At(value = "HEAD"), cancellable = true)
    public void replaced_get_model(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof ArmorItem) {
            //System.out.println("ArmorItem_getModel: "+stack.getItem());
            cir.setReturnValue(DynamicTrimClient.DYNAMIC_ARMOR_MODEL_GENERATOR.getModel(stack));
        }
    }
}
