package space.ryzhenkov.fabric2discord.ktmixins

import net.minecraft.network.message.SignedMessage
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import space.ryzhenkov.fabric2discord.KordInstance
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils

object ServerPlayNetworkHandlerMixin {
    fun onPlayerMessageEvent(player: ServerPlayerEntity, signedMessage: SignedMessage) {
        if (!ConfigAPI.messages.chatMessage.enabled) return

        val replacements = hashMapOf<String, String>()
        replacements["message"] = signedMessage.content.string

        if (ConfigAPI.general.ids.getWebhookOrNull() != null) {
            KordInstance.executeWebhook(
                MessageUtils.format(ConfigAPI.messages.chatMessage.header, player, replacements).string,
                MessageUtils.format(ConfigAPI.messages.chatMessage.iconHeader, player, replacements).string,
                MessageUtils.format(ConfigAPI.messages.chatMessage.message, player, replacements).string,
            )
            return
        }

        if (ConfigAPI.general.ids.getChatChannelOrNull() == null) return

        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getChatChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.chatMessage, player, replacements)
        }
    }

    fun remove(player: ServerPlayerEntity, reason: Text) {
        if (!ConfigAPI.messages.playerLeave.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() == null) return

        val replacements = hashMapOf<String, String>()
        replacements["leave_reason"] = reason.string

        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.playerLeave, player, replacements)
        }
    }
}