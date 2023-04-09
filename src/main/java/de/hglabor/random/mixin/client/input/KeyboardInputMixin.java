package de.hglabor.random.mixin.client.input;

import net.fabricmc.example.subwaysurfers.SubwaySurfersManager;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;movementForward:F", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void subwaySurfersMoveInjection(KeyboardInput instance, float value) {
        instance.movementForward = SubwaySurfersManager.INSTANCE.getMoveForward() ? 1.0F : value;
        if (SubwaySurfersManager.INSTANCE.isEnabled()) {
            instance.pressingLeft = false;
            instance.pressingRight = false;
        }
    }
}
