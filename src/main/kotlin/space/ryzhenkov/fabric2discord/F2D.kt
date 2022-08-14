package space.ryzhenkov.fabric2discord


import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.lortseam.completeconfig.data.Config
import net.fabricmc.api.ModInitializer
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.PlaceholderUtils

class F2D : ModInitializer {
    override fun onInitialize() {
        config.load()
        if (ConfigAPI.configVersion != 2) logger.warn(
            "\n\nYou have outdated config version, this might cause errors!\n" +
                    "Be sure to check that your config matches this file style:\n" +
                    "https://github.com/rogi27/Fabric2Discord/wiki/Configuration\n"
        )
        if (ConfigAPI.general.token.isEmpty()) throw Error("You must provide Discord API token for this mod to work!")

        KordInstance.launch()

        if (ConfigAPI.general.ids.webhook.toInt() == -1) {
            logger.warn("Your webhook value is -1, creating new webhook for chat #${ConfigAPI.general.ids.getChatChannelOrNull()}...")
            if (ConfigAPI.general.ids.getChatChannelOrNull() != null) {
                runBlocking {
                    val webhook =
                        KordInstance.kord.getChannelOf<TextChannel>(ConfigAPI.general.ids.getChatChannelOrNull()!!)!!
                            .createWebhook(
                                "Fabric2Discord Webhook"
                            ) { this.name = "Fabric2Discord" }
                    ConfigAPI.general.ids.webhook = webhook.id.value.toLong()

                    config.save()
                    config.load()
                }
            } else {
                logger.warn("Could not create webhook due to `chatChannel` being disabled. Setting webhook value as 0...")
                ConfigAPI.general.ids.webhook = 0

                config.save()
                config.load()
            }
        }

        if (ConfigAPI.general.ids.webhook.toInt() == 0)
            logger.info("Webhook is disabled because its value is 0")
        else
            logger.info("Using webhook with id `${ConfigAPI.general.ids.webhook}`")

        CommandHandler.init()
        PlaceholderUtils.init()
    }

    companion object {
        val scope = CoroutineScope(Dispatchers.IO)
        var minecraftServer: MinecraftServer? = null
        var logger: Logger = LogManager.getLogger("Fabric2Discord")
        val config: Config = Config("f2d", arrayOf("settings"), ConfigAPI)
    }
}

