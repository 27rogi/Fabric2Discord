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
import space.ryzhenkov.Fabric2Discord.ClientInstance;
import space.ryzhenkov.Fabric2Discord.ConfigInstance;
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
        ClientInstance.INSTANCE.setMinecraftServer(this.getPlayerManager().getServer());

        if (ConfigInstance.general.ids.INSTANCE.getLogChannel() == null) return;
        MessageUtils.INSTANCE.sendEmbedMessage(ConfigInstance.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(ConfigInstance.messages.serverStart.INSTANCE, ClientInstance.INSTANCE.getMinecraftServer(), null, null)
                        .build().asRequest()
        );

        if (ConfigInstance.general.status.INSTANCE.getEnabled()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    var presence = MessageUtils.INSTANCE.format(
                            ConfigInstance.general.status.INSTANCE.getVariants()[(int) (System.currentTimeMillis() % ConfigInstance.general.status.INSTANCE.getVariants().length)],
                            ClientInstance.INSTANCE.getMinecraftServer(), null
                    ).getString();
                    ClientInstance.INSTANCE.getLoggedClient().updatePresence(ClientPresence.of(
                            Status.valueOf(ConfigInstance.general.status.INSTANCE.getType()),
                            // TODO: Add activity type selection in config
                            ClientActivity.playing(presence)
                    )).subscribe();
                    F2D.logger.debug("Presence was updated to `" + presence + "`");
                }
            }, TimeUnit.SECONDS.toMillis(1), TimeUnit.MINUTES.toMillis(ConfigInstance.general.status.INSTANCE.getInterval()));
        }
    }

    @Inject(at = @At("TAIL"), method = "shutdown")
    private void afterShutdownServer(CallbackInfo info) {
        if (ConfigInstance.general.ids.INSTANCE.getLogChannel() != null) {
            MessageUtils.INSTANCE.sendEmbedMessage(ConfigInstance.general.ids.INSTANCE.getLogChannel(),
                    MessageUtils.INSTANCE.getConfigMessage(ConfigInstance.messages.serverStop.INSTANCE, ClientInstance.INSTANCE.getMinecraftServer(), null, null)
                            .build().asRequest()
            );
        }

        if (ConfigInstance.general.status.INSTANCE.getEnabled()) timer.cancel();
        ClientInstance.INSTANCE.getLoggedClient().logout().block();
    }
}
