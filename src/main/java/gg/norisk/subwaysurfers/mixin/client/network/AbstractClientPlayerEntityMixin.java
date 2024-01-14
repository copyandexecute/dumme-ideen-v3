package gg.norisk.subwaysurfers.mixin.client.network;

import gg.norisk.subwaysurfers.SubwaySurfers;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(method = "getSkinTexture", at = @At("RETURN"), cancellable = true)
    private void noriskSkin(CallbackInfoReturnable<Identifier> cir) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            cir.setReturnValue(SubwaySurfers.INSTANCE.getNoriskSkin());
        }
    }
}
