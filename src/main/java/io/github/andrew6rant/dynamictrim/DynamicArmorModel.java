package io.github.andrew6rant.dynamictrim;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.andrew6rant.dynamictrim.DynamicTrimClient.isStackedTrimsEnabled;

@Environment(EnvType.CLIENT)
public record DynamicArmorModel(String armorType, Identifier resourceId) implements UnbakedModel, BakedModel, FabricBakedModel {
    private static final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[]{
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/furnace_front_on")),
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/furnace_top"))
    };
    private static Sprite[] SPRITES = new Sprite[2];
    private static Mesh mesh;
    //public static Function<SpriteIdentifier, Sprite> textureGetter2;

    private static Gson gson = new Gson();


    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        //for (Identifier dependency : getModelDependencies()) {
        //    modelLoader.apply(dependency).setParents(modelLoader);
        //}
    }


        @Nullable
        @Override
        public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {

            //textureGetter2 = textureGetter; // yes, I know this is atrocious code
            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();

            SPRITES[0] = textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, resourceId));
            emitter.square(Direction.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            //emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
            //emitter.spriteBake(0, , MutableQuadView.BAKE_NORMALIZED);
            emitter.spriteColor(0, -1, -1, -1, -1);

            //ItemStack stack = resourceId.
            //ItemStack.fromNbt()
            ItemStack stack = Registries.ITEM.get(resourceId).getDefaultStack();

            //Sprite sprite = textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/stone")));

            //SpriteContents spriteContents = new SpriteContents(new Identifier("minecraft:block/stone"), new SpriteDimensions(16, 16), NativeImage.read(new Identifier("minecraft:block/stone").getPath(), MinecraftClient.getInstance().getResourceManager()));
            emitter.emit();
            mesh = builder.build();
            return this;


        }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, @NotNull RenderContext context) {
        /*
        QuadEmitter emitter = context.getEmitter();
        Identifier id = stack.getItem().getRegistryEntry().registryKey().getValue();
        Identifier newId = new Identifier(id.getNamespace(), "item/" + id.getPath());
        SPRITES[0] = textureGetter2.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, newId));
        emitter.square(Direction.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        if(stack.getNbt().contains("Trim")) {
            if (isStackedTrimsEnabled) {       // If StackedTrims mod is installed, iterate through the list of trims
                assert stack.getNbt() != null; // this code is modified from StackedTrims' ArmorTrimItemMixin
                NbtList nbtList = stack.getNbt().getList("Trim", 10);
                for (NbtElement nbtElement : nbtList) {
                    //System.out.println(nbtElement);
                    JsonObject trimJSON = gson.fromJson(nbtElement.asString(), JsonObject.class);
                    String id_and_material = trimJSON.get("material").getAsString();
                    String id_and_pattern = trimJSON.get("pattern").getAsString();
                    String material = id_and_material.substring(id_and_material.indexOf(":")+1);
                    String pattern = id_and_pattern.substring(id_and_pattern.indexOf(":")+1);
                    SPRITES[1] = textureGetter2.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:trims/items/"+armorType+"_trim_"+pattern+"_"+material)));
                    emitter.square(Direction.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, -1.0f);
                    emitter.spriteBake(0, SPRITES[1], MutableQuadView.BAKE_LOCK_UV);
                    emitter.spriteColor(0, -1, -1, -1, -1);
                    emitter.emit();
                }
                //System.out.println("-----------");
            } else {
                JsonObject trimJSON = gson.fromJson(stack.getNbt().get("Trim").asString(), JsonObject.class);
                String id_and_material = trimJSON.get("material").getAsString();
                String id_and_pattern = trimJSON.get("pattern").getAsString();
                String material = id_and_material.substring(id_and_material.indexOf(":")+1);
                String pattern = id_and_pattern.substring(id_and_pattern.indexOf(":")+1);
                SPRITES[1] = textureGetter2.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:trims/items/"+armorType+"_trim_"+pattern+"_"+material)));
                emitter.square(Direction.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, -1.0f);
                emitter.spriteBake(0, SPRITES[1], MutableQuadView.BAKE_LOCK_UV);
                emitter.spriteColor(0, -1, -1, -1, -1);
                emitter.emit();
            }
        }*/
        context.meshConsumer().accept(mesh);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return SPRITES[0];
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
