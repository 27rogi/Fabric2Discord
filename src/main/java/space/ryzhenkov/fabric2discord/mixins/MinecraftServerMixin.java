package space.ryzhenkov.fabric2discord.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Timer;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    Timer timer = new Timer();

    @Shadow
    private PlayerManager playerManager;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {
        space.ryzhenkov.fabric2discord.ktmixins.MinecraftServerMixin.INSTANCE.afterSetupServer(playerManager, timer);
    }

    @Inject(at = @At("TAIL"), method = "shutdown")
    private void afterShutdownServer(CallbackInfo info) {
        space.ryzhenkov.fabric2discord.ktmixins.MinecraftServerMixin.INSTANCE.afterShutdownServer(timer);
    }
}
