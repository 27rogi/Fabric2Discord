package su.rogi.fabric2discord.mixin

import net.minecraft.server.network.ServerPlayerEntity
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.utils.MessageUtils

object PlayerManagerMixinKotlin {
    fun onPlayerConnect(player: ServerPlayerEntity) {
        if (!Configs.MESSAGES.entries.player.joined.enabled) return
        if (Configs.SETTINGS.entries.ids.getLogChannel() == null) return
        MessageUtils.sendEmbedMessage(Configs.SETTINGS.entries.ids.getLogChannel()) {
            Configs.MESSAGES.entries.player.joined.getEmbed(player = player)
        }
    }
}