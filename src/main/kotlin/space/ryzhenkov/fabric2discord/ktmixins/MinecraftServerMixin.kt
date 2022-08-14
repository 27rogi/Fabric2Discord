package space.ryzhenkov.fabric2discord.ktmixins

import kotlinx.coroutines.runBlocking
import net.minecraft.server.PlayerManager
import space.ryzhenkov.fabric2discord.F2D
import space.ryzhenkov.fabric2discord.KordInstance
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils
import java.util.*
import java.util.concurrent.TimeUnit

object MinecraftServerMixin {
    fun afterSetupServer(playerManager: PlayerManager, timer: Timer) {
        if (!ConfigAPI.messages.serverStart.enabled) return
        F2D.minecraftServer = playerManager.server

        if (ConfigAPI.general.ids.getChatChannelOrNull() != null) F2D.logger.info("Enabled sync between game chat and Discord!")
        if (ConfigAPI.general.ids.getLogChannelOrNull() != null) {
            MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
                MessageUtils.getEmbedMessage(ConfigAPI.messages.serverStart, F2D.minecraftServer, null)
            }
        }
        if (!ConfigAPI.general.status.enabled) return
        timer.schedule(object : TimerTask() {
            override fun run() {
                val presence =
                    MessageUtils.format(ConfigAPI.general.status.variants.random(), F2D.minecraftServer).string
                runBlocking { KordInstance.kord.editPresence { this.playing(presence) } }
                F2D.logger.debug("Presence was updated to \"$presence\"")
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.MINUTES.toMillis(ConfigAPI.general.status.interval.toLong()))
    }

    fun afterShutdownServer(timer: Timer) {
        if (!ConfigAPI.messages.serverStop.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() != null) {
            MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
                MessageUtils.getEmbedMessage(ConfigAPI.messages.serverStop, F2D.minecraftServer, null)
            }
        }
        if (ConfigAPI.general.status.enabled) timer.cancel()
        runBlocking { KordInstance.kord.logout() }
        F2D.logger.info("Disconnected from Discord.")
    }
}