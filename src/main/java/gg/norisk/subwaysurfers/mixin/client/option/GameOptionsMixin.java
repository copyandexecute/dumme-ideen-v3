package gg.norisk.subwaysurfers.mixin.client.option;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfersManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Inject(method = "setPerspective", at = @At("HEAD"), cancellable = true)
    private void subwaySurfersStaticPerspective(Perspective perspective, CallbackInfo ci) {
        if (SubwaySurfersManager.INSTANCE.isEnabled()) ci.cancel();
    }
}
