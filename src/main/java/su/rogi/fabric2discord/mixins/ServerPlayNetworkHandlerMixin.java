package su.rogi.fabric2discord.mixins;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.ServerPlayNetworkHandlerMixinKotlin;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getPlayerManager()Lnet/minecraft/server/PlayerManager;"))
    private void onPlayerMessageEvent(SignedMessage signedMessage, CallbackInfo ci) {
        ServerPlayNetworkHandlerMixinKotlin.INSTANCE.onPlayerMessageEvent(player, signedMessage);
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void remove(Text reason, CallbackInfo ci) {
        ServerPlayNetworkHandlerMixinKotlin.INSTANCE.remove(player, reason);
    }
}
