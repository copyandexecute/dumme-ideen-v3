package gg.norisk.subwaysurfers.mixin.client.render;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfersManager;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @ModifyConstant(method = "update", constant = @Constant(doubleValue = 4.0))
    private double subwaySurfersIncreaseCameraDistance(double constant) {
        return (SubwaySurfersManager.INSTANCE.isEnabled()) ? SubwaySurfersManager.INSTANCE.getDesiredCameraDistance() : constant;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
    private void subwaySurfersStaticPerspective(Args args) {
        if (SubwaySurfersManager.INSTANCE.isEnabled()) {
            args.set(0, SubwaySurfersManager.INSTANCE.getYawAndPitch().getFirst());
            args.set(1, SubwaySurfersManager.INSTANCE.getYawAndPitch().getSecond());
        }
    }
}
