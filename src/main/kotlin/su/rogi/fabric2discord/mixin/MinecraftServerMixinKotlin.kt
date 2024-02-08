package su.rogi.fabric2discord.mixin

import dev.kord.common.entity.PresenceStatus
import kotlinx.coroutines.runBlocking
import net.minecraft.server.PlayerManager
import su.rogi.fabric2discord.Fabric2Discord
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.kord.KordClient
import su.rogi.fabric2discord.utils.MessageUtils
import java.util.*
import java.util.concurrent.TimeUnit

object MinecraftServerMixinKotlin {
    fun afterSetupServer(playerManager: PlayerManager, timer: Timer) {
        Fabric2Discord.minecraftServer = playerManager.server

        if (Configs.MESSAGES.entries.server.started.enabled) {
            MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.SERVER_STATUS)) {
                Configs.MESSAGES.entries.server.started.let {
                    suppressNotifications = it.silent
                    embeds = mutableListOf(it.getEmbed(null, server = Fabric2Discord.minecraftServer))
                }
            }
        }

        Fabric2Discord.logger.info("Enabled sync between game chat and Discord!")

        if (!Configs.SETTINGS.entries.status.enabled) return
        timer.schedule(object : TimerTask() {
            override fun run() {
                val presence =
                    MessageUtils.format(Configs.SETTINGS.entries.status.variants.random(), server = Fabric2Discord.minecraftServer)?.string
                        ?: return
                runBlocking { KordClient.kord.editPresence {
                    when (Configs.SETTINGS.entries.status.type) {
                        "IDLE" -> this.status = PresenceStatus.Idle
                        "DO_NOT_DISTURB" -> this.status = PresenceStatus.DoNotDisturb
                        "INVISIBLE" -> this.status = PresenceStatus.Invisible
                        "OFFLINE" -> this.status = PresenceStatus.Offline
                        else -> this.status = PresenceStatus.Online
                    }
                    when (Configs.SETTINGS.entries.status.action) {
                        "LISTENING" -> this.listening(presence)
                        "COMPETING" -> this.competing(presence)
                        "STREAMING" -> this.streaming(presence, Configs.SETTINGS.entries.status.url)
                        else -> this.playing(presence)
                    }
                } }
                Fabric2Discord.logger.debug("Presence was updated to \"$presence\"")
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.MINUTES.toMillis(Configs.SETTINGS.entries.status.interval.toLong()))
    }

    fun afterShutdownServer(timer: Timer) {
        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.SERVER_STATUS)) {
            Configs.MESSAGES.entries.server.stopped.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(null, server = Fabric2Discord.minecraftServer))
            }
        }
        if (Configs.SETTINGS.entries.status.enabled) timer.cancel()
        Fabric2Discord.logger.info("Shutting down kord and listeners...")
        KordClient.stop()
    }

}