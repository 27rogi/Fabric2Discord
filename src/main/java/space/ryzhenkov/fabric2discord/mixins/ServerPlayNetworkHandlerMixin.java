package space.ryzhenkov.fabric2discord.mixins;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void onPlayerMessageEvent(TextStream.Message message, CallbackInfo ci) {
        space.ryzhenkov.fabric2discord.ktmixins.ServerPlayNetworkHandlerMixin.INSTANCE.onPlayerMessageEvent(player, message);
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void remove(Text reason, CallbackInfo ci) {
        space.ryzhenkov.fabric2discord.ktmixins.ServerPlayNetworkHandlerMixin.INSTANCE.remove(player, reason);
    }
}