package space.ryzhenkov.Fabric2Discord.mixins;

import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ryzhenkov.Fabric2Discord.F2DClient;
import space.ryzhenkov.Fabric2Discord.config.F2DConfig;
import space.ryzhenkov.Fabric2Discord.F2D;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    Timer timer = new Timer();

    @Shadow
    public abstract ServerMetadata getServerMetadata();

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {
        F2DClient.INSTANCE.setMinecraftServer(this.getPlayerManager().getServer());

        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;
        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.serverStart.INSTANCE, F2DClient.INSTANCE.getMinecraftServer(), null, null)
                        .build().asRequest()
        );

        if (F2DConfig.general.status.INSTANCE.getEnabled()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    var presence = MessageUtils.INSTANCE.format(
                            F2DConfig.general.status.INSTANCE.getVariants()[(int) (System.currentTimeMillis() % F2DConfig.general.status.INSTANCE.getVariants().length)],
                            F2DClient.INSTANCE.getMinecraftServer(), null
                    ).getString();
                    F2DClient.INSTANCE.getLoggedClient().updatePresence(ClientPresence.of(
                            Status.valueOf(F2DConfig.general.status.INSTANCE.getType()),
                            // TODO: Add activity type selection in config
                            ClientActivity.playing(presence)
                    )).subscribe();
                    F2D.logger.debug("Presence was updated to `" + presence + "`");
                }
            }, TimeUnit.SECONDS.toMillis(1), TimeUnit.MINUTES.toMillis(F2DConfig.general.status.INSTANCE.getInterval()));
        }
    }

    @Inject(at = @At("TAIL"), method = "shutdown")
    private void afterShutdownServer(CallbackInfo info) {
        if (F2DConfig.general.ids.INSTANCE.getLogChannel() != null) {
            MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                    MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.serverStop.INSTANCE, F2DClient.INSTANCE.getMinecraftServer(), null, null)
                            .build().asRequest()
            );
        }

        if (F2DConfig.general.status.INSTANCE.getEnabled()) timer.cancel();
        F2DClient.INSTANCE.getLoggedClient().logout().block();
    }
}
