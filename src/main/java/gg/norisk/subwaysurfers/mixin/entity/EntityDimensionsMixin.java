package gg.norisk.subwaysurfers.mixin.entity;

import gg.norisk.subwaysurfers.entity.ModifiedEntityDimensions;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDimensions.class)
public abstract class EntityDimensionsMixin implements ModifiedEntityDimensions {
    @Shadow
    @Final
    public float width;
    @Shadow
    @Final
    public float height;
    @Unique
    private float length = -1;

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public void setLength(float v) {
        this.length = v;
    }

    @Inject(method = "getBoxAt(DDD)Lnet/minecraft/util/math/Box;", at = @At("RETURN"), cancellable = true)
    private void injected(double d, double e, double f, CallbackInfoReturnable<Box> cir) {
        if (length != -1) {
            float boxLength = this.length / 2.0F;
            float boxWidth = this.width / 2.0F;
            float boxHeight = this.height;
            var box = new Box(
                    d - (double) boxWidth,
                    e,
                    f - (double) boxLength, //LENGTH
                    d + (double) boxWidth,
                    e + (double) boxHeight,
                    f + (double) boxLength //LENGTH
            );
            cir.setReturnValue(box);
        }
    }
}
