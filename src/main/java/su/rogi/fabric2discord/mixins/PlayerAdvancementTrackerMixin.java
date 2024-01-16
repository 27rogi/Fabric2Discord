package su.rogi.fabric2discord.mixins;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.rogi.fabric2discord.mixin.PlayerAdvancementTrackerMixinKotlin;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void grantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat()) {
            PlayerAdvancementTrackerMixinKotlin.INSTANCE.grantCriterion(owner, advancement, this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS));
        }
    }
}
