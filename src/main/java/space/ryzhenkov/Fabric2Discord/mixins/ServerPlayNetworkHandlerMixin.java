package space.ryzhenkov.Fabric2Discord.mixins;

import discord4j.discordjson.json.WebhookExecuteRequest;
import discord4j.rest.util.MultipartRequest;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ryzhenkov.Fabric2Discord.F2DClient;
import space.ryzhenkov.Fabric2Discord.config.F2DConfig;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Shadow protected abstract CompletableFuture<FilteredMessage> filterText(String text);

    @Inject(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getPlayerManager()Lnet/minecraft/server/PlayerManager;"))
    private void onPlayerMessageEvent(SignedMessage signedMessage, CallbackInfo ci) {
        if (!F2DConfig.messages.chatMessage.INSTANCE.getEnabled()) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("message", signedMessage.getContent().getString());

        if (F2DConfig.general.ids.INSTANCE.getWebhookId() != null) {
            F2DClient.INSTANCE.getClient().getWebhookService().getWebhook(F2DConfig.general.ids.INSTANCE.getWebhookId().asLong()).subscribe(webhook -> {
                MultipartRequest<WebhookExecuteRequest> request = MultipartRequest.ofRequest(
                        WebhookExecuteRequest.builder()
                                .avatarUrl(MessageUtils.INSTANCE.format(F2DConfig.messages.chatMessage.INSTANCE.getIconHeader(), player, null).getString())
                                .username(MessageUtils.INSTANCE.format(F2DConfig.messages.chatMessage.INSTANCE.getHeader(), player, null).getString())
                                .content(MessageUtils.INSTANCE.format(F2DConfig.messages.chatMessage.INSTANCE.getMessage(), this.player, replacements).getString()
                                ).build()
                );
                F2DClient.INSTANCE.getClient().getWebhookService()
                        .executeWebhook(F2DConfig.general.ids.INSTANCE.getWebhookId().asLong(), webhook.token().get(), false, request)
                        .block();
            });
            return;
        }

        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;
        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.chatMessage.INSTANCE, null, this.player, replacements)
                        .build().asRequest()
        );
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void remove(Text reason, CallbackInfo ci) {
        if (!F2DConfig.messages.playerLeave.INSTANCE.getEnabled()) return;
        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("leave_reason", reason.getString());

        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.playerLeave.INSTANCE, null, getPlayer(), replacements)
                        .build().asRequest()
        );
    }
}
