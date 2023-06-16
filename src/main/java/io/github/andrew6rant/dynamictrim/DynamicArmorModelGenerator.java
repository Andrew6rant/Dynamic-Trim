package io.github.andrew6rant.dynamictrim;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.github.andrew6rant.dynamictrim.DynamicTrimClient.isStackedTrimsEnabled;

//Massive thanks to DasBlackfur for letting me use some code from FerociousForgery mod.
// https://github.com/DasBlackfur/FerociousForgery
public class DynamicArmorModelGenerator {

    public static Function<SpriteIdentifier, Sprite> textureGetter;

    private static Gson gson = new Gson();
    public static Map<NbtCompound, BakedModel> MODEL_CACHE = new HashMap<>();
    public static Map<NbtCompound, Identifier> TEXTURE_CACHE = new HashMap<>();

    public static Map<Sprite, Identifier> TEXTURE_CACHE_SPRITE = new HashMap<>();

    public void applyTrim(JsonObject trimJSON, Identifier id) {
        String id_and_material = trimJSON.get("material").getAsString();
        String id_and_pattern = trimJSON.get("pattern").getAsString();
        String material = id_and_material.substring(id_and_material.indexOf(":")+1);
        String pattern = id_and_pattern.substring(id_and_pattern.indexOf(":")+1);
        String armorType = "helmet";
        TEXTURE_CACHE_SPRITE.put(textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:trims/items/"+armorType+"_trim_"+pattern+"_"+material))),
                id);
    }

    public BakedModel getModel(ItemStack stack) {
        Identifier id = stack.getItem().getRegistryEntry().registryKey().getValue();
        Identifier newId = new Identifier(id.getNamespace(), "item/" + id.getPath());

        if(stack.getNbt().contains("Trim")) {
            if (isStackedTrimsEnabled) {       // If StackedTrims mod is installed, iterate through the list of trims
                assert stack.getNbt() != null; // this code is modified from StackedTrims' ArmorTrimItemMixin
                NbtList nbtList = stack.getNbt().getList("Trim", 10);
                for (NbtElement nbtElement : nbtList) {
                    //System.out.println(nbtElement);
                    JsonObject trimJSON = gson.fromJson(nbtElement.asString(), JsonObject.class);
                    applyTrim(trimJSON, id);
                }
            } else {
                System.out.println("StackedTrims is not installed, but trim NBT was found on an armor item!");
            }
        }

        for(Sprite sprite : TEXTURE_CACHE_SPRITE.keySet()) {
            System.out.println(sprite);
        }


        return MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("minecraft", "iron_shovel", "inventory"));

        /*
        if ((!MODEL_CACHE.containsKey(stack.getOrCreateNbt())) || (!TEXTURE_CACHE.containsKey(stack.getOrCreateNbt()))) {



            /*
            TEXTURE_CACHE.put(stack.getOrCreateNbt(),
                    MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("ff-blank",
                            new NativeImageBackedTexture(getImage(stack))));

            Map<String, Either<SpriteIdentifier, String>> tmpMap = new HashMap<>();
            tmpMap.put("layer0", Either.left(new SpriteIdentifier(TEXTURE_CACHE.get(stack.getOrCreateNbt()),
                    TEXTURE_CACHE.get(stack.getOrCreateNbt()))));

            BakedModel handheldModel = MinecraftClient.getInstance().getBakedModelManager().getModel(
                    new ModelIdentifier("minecraft", "iron_shovel", "inventory"));

            JsonUnbakedModel tmpModel = ModelLoader.ITEM_MODEL_GENERATOR.create((spriteIdentifier -> getSprite(stack)),
                    new JsonUnbakedModel(null,
                            new ArrayList<>(),
                            tmpMap, null,
                            JsonUnbakedModel.GuiLight.ITEM,
                            handheldModel.getTransformation(),
                            new ArrayList<>()));

            MODEL_CACHE.put(stack.getOrCreateNbt(),
                    tmpModel.bake(new DummyBaker(tmpModel), (id) -> getSprite(stack), ModelRotation.X0_Y0,
                            new Identifier("troll:troll")));

        } return MODEL_CACHE.get(stack.getOrCreateNbt());*/
    }

    public Sprite getSprite(ItemStack stack) {
        return new Sprite(TEXTURE_CACHE.get(stack.getOrCreateNbt()),
                new SpriteContents(new Identifier("minecraft:dynamic/ff-blank_1"),
                        new SpriteDimensions(16, 16), getImage(stack),
                        new AnimationResourceMetadata(
                                ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)), 16,
                                16, 1, false)), 16, 16, 0, 0);
    }

    public NativeImage getImage(ItemStack stack) {
        int width = 16;
        int height = 16;
        NativeImage nativeImage = new NativeImage(width, height, false);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                assert stack.getNbt() != null;
                //nativeImage.setColor(i, j, getColor(
                //                        FormingAnvilScreen.getColor(stack.getNbt().getInt("materialID"), stack.getNbt().getInt("heat"),
                //                                                    stack.getNbt().getIntArray("Model")[i * 16 + j])))
                nativeImage.setColor(i, j, -14745601); // -1
            }
        }

        return nativeImage;
    }

    /*
    public static int getColor(int material) {
        return material;
    }
     */
}
