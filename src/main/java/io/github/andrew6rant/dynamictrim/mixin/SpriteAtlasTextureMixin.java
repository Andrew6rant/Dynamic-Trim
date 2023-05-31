package io.github.andrew6rant.dynamictrim.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin extends AbstractTexture {
    @Shadow private Map<Identifier, Sprite> sprites = Map.of();
    @Shadow
    public void tickAnimatedSprites() {

    }

    public void tickStaticSprites() {
        this.bindTexture();
        Iterator<Map.Entry<Identifier, Sprite>> itr = sprites.entrySet().iterator();
        while(itr.hasNext()) {
            Sprite.TickableAnimation tickableAnimation = (Sprite.TickableAnimation)itr.next();
            tickableAnimation.tick();
        }

        /*
        for (Map.Entry<Identifier, Sprite> identifierSpriteEntry : sprites.entrySet()) {
            Sprite.TickableAnimation tickableAnimation = (Sprite.TickableAnimation) identifierSpriteEntry;
            tickableAnimation.tick();
        }
        */
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::tickAnimatedSprites);
            RenderSystem.recordRenderCall(this::tickStaticSprites);
        } else {
            this.tickAnimatedSprites();
            this.tickStaticSprites();
        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
    }
}
