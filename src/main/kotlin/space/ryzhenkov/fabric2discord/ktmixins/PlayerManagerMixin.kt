package space.ryzhenkov.fabric2discord.ktmixins

import net.minecraft.server.network.ServerPlayerEntity
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils

object PlayerManagerMixin {
    fun onPlayerConnect(player: ServerPlayerEntity) {
        if (!ConfigAPI.messages.playerJoin.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() == null) return
        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.playerJoin, player, null)
        }
    }
}