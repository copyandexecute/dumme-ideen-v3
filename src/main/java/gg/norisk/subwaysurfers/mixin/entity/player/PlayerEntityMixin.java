package gg.norisk.subwaysurfers.mixin.entity.player;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements SubwaySurfer {
    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    private static final TrackedData<Integer> RAIL = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SLIDING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SLIDING.equals(data)) {
            calculateDimensions();
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void slidingHitboxInjection(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (isSliding()) cir.setReturnValue(EntityDimensions.fixed(0.2f, 0.2f));
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerInjection(CallbackInfo ci) {
        this.dataTracker.startTracking(RAIL, 1);
        this.dataTracker.startTracking(SLIDING, false);
        this.dataTracker.startTracking(SubwaySurferKt.getCoinDataTracker(), 0);
        this.dataTracker.startTracking(SubwaySurferKt.getSubwaySurfersTracker(), false);
    }

    @Override
    public int getRail() {
        return this.dataTracker.get(RAIL);
    }

    @Override
    public void setRail(int i) {
        this.dataTracker.set(RAIL, MathHelper.clamp(i, 1, 3));
    }

    @Override
    public void setSliding(boolean b) {
        this.dataTracker.set(SLIDING, b);
    }

    @Override
    public boolean isSliding() {
        return this.dataTracker.get(SLIDING);
    }
}
