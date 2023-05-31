package io.github.andrew6rant.dynamictrim.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin {
    @Shadow private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow private final Identifier id;



    public SpriteContentsMixin(Identifier id) {
        this.id = id;
    }
    /*
    @Inject(at = @At("RETURN"), method = "createAnimation(Lnet/minecraft/client/texture/SpriteDimensions;IILnet/minecraft/client/resource/metadata/AnimationResourceMetadata;)Lnet/minecraft/client/texture/SpriteContents$Animation;", cancellable = true)
    public void createAnimation(SpriteDimensions dimensions, int imageWidth, int imageHeight, AnimationResourceMetadata metadata, CallbackInfoReturnable<SpriteContents.Animation> cir) {
        List<SpriteContents.AnimationFrame> list = new ArrayList();
        metadata.forEachFrame((index, frameTime) -> {
            list.add(new SpriteContents.AnimationFrame(index, frameTime));
        });
        if (cir.getReturnValue() == null) {
            System.out.println("NULLLLL!!!! "+cir.getReturnValue());
        } else {
            System.out.println("testinggggg: "+cir.getReturnValue());
        }
    }*/
        /**
         * @author
         * @reason
         */

    @Overwrite
    public SpriteContents.Animation createAnimation(SpriteDimensions dimensions, int imageWidth, int imageHeight, AnimationResourceMetadata metadata, CallbackInfoReturnable<SpriteContents.Animation> cir) {
        int i = imageWidth / dimensions.width();
        int j = imageHeight / dimensions.height();
        int k = i * j;
        List<SpriteContents.AnimationFrame> list = new ArrayList();
        metadata.forEachFrame((index, frameTime) -> {
            list.add(new SpriteContents.AnimationFrame(index, frameTime));
        });
        int l;
        if (list.isEmpty()) {
            for(l = 0; l < k; ++l) {
                list.add(new SpriteContents.AnimationFrame(l, metadata.getDefaultFrameTime()));
            }
        } else {
            l = 0;
            IntSet intSet = new IntOpenHashSet();

            for(Iterator<SpriteContents.AnimationFrame> iterator = list.iterator(); iterator.hasNext(); ++l) {
                SpriteContents.AnimationFrame animationFrame = (SpriteContents.AnimationFrame)iterator.next();
                boolean bl = true;
                if (animationFrame.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.id, l, animationFrame.time});
                    bl = false;
                }

                if (animationFrame.index < 0 || animationFrame.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.id, l, animationFrame.index});
                    bl = false;
                }

                if (bl) {
                    intSet.add(animationFrame.index);
                } else {
                    iterator.remove();
                }
            }

            int[] is = IntStream.range(0, k).filter((ix) -> {
                return !intSet.contains(ix);
            }).toArray();
            if (is.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", this.id, Arrays.toString(is));
            }
        }

        return list.size() <= 1 ? null : new SpriteContents.Animation(ImmutableList.copyOf(list), i, metadata.shouldInterpolate());
    }
}
