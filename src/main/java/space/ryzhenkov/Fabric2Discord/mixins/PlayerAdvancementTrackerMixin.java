package space.ryzhenkov.Fabric2Discord.mixins;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.ryzhenkov.Fabric2Discord.config.F2DConfig;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

import java.util.HashMap;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    String advancementString;

    @Shadow
    private ServerPlayerEntity owner;

    @Shadow
    @Final
    private PlayerManager playerManager;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void grantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!F2DConfig.messages.playerAdvancement.INSTANCE.getEnabled()) return;
        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("advancement_name", advancement.getDisplay().getTitle().getString());
        replacements.put("advancement_description", advancement.getDisplay().getDescription().getString());
        replacements.put("advancement_id", advancement.getDisplay().getFrame().getId());

        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.playerAdvancement.INSTANCE, null, owner, replacements)
                        .build().asRequest()
        );
    }
}
