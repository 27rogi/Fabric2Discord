package space.ryzhenkov.Fabric2Discord.mixins;

import discord4j.discordjson.json.WebhookExecuteRequest;
import discord4j.rest.util.MultipartRequest;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ryzhenkov.Fabric2Discord.ClientInstance;
import space.ryzhenkov.Fabric2Discord.ConfigInstance;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

import java.util.HashMap;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Inject(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void onPlayerMessageEvent(TextStream.Message message, CallbackInfo ci) {
        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("message", message.getFiltered());

        if (ConfigInstance.general.ids.INSTANCE.getWebhookId() != null) {
            ClientInstance.INSTANCE.getClient().getWebhookService().getWebhook(ConfigInstance.general.ids.INSTANCE.getWebhookId().asLong()).subscribe(webhook -> {
                MultipartRequest<WebhookExecuteRequest> request = MultipartRequest.ofRequest(
                        WebhookExecuteRequest.builder()
                                .avatarUrl(MessageUtils.INSTANCE.format(ConfigInstance.messages.chatMessage.INSTANCE.getIconHeader(), player, null).getString())
                                .username(MessageUtils.INSTANCE.format(ConfigInstance.messages.chatMessage.INSTANCE.getHeader(), player, null).getString())
                                .content(MessageUtils.INSTANCE.format(ConfigInstance.messages.chatMessage.INSTANCE.getMessage(), this.player, replacements).getString()
                                ).build()
                );
                ClientInstance.INSTANCE.getClient().getWebhookService()
                        .executeWebhook(ConfigInstance.general.ids.INSTANCE.getWebhookId().asLong(), webhook.token().get(), false, request)
                        .block();
            });
            return;
        }

        if (ConfigInstance.general.ids.INSTANCE.getLogChannel() == null) return;
        MessageUtils.INSTANCE.sendEmbedMessage(ConfigInstance.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(ConfigInstance.messages.chatMessage.INSTANCE, null, this.player, replacements)
                        .build().asRequest()
        );
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void remove(Text reason, CallbackInfo ci) {
        if (ConfigInstance.general.ids.INSTANCE.getLogChannel() == null) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("leave_reason", reason.getString());

        MessageUtils.INSTANCE.sendEmbedMessage(ConfigInstance.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(ConfigInstance.messages.playerLeave.INSTANCE, null, getPlayer(), replacements)
                        .build().asRequest()
        );
    }
}
