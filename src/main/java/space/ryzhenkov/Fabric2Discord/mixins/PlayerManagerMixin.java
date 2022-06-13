package space.ryzhenkov.Fabric2Discord.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ryzhenkov.Fabric2Discord.ConfigInstance;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (ConfigInstance.general.ids.INSTANCE.getLogChannel() == null) return;
        MessageUtils.INSTANCE.sendEmbedMessage(ConfigInstance.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(ConfigInstance.messages.playerJoin.INSTANCE, null, player, null)
                        .build().asRequest()
        );
    }
}
