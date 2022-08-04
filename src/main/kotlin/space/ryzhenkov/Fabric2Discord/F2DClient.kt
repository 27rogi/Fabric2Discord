package space.ryzhenkov.Fabric2Discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import eu.pb4.placeholders.api.TextParserUtils
import net.minecraft.server.MinecraftServer
import space.ryzhenkov.Fabric2Discord.config.F2DConfig
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils


object F2DClient {
    
    var client: DiscordClient = DiscordClient.create(F2DConfig.general.token)
    var loggedClient: GatewayDiscordClient? = client.login().block()
    var minecraftServer: MinecraftServer? = null
    
    fun init() {
        if (loggedClient == null) throw Exception("Discord client is not connected!")
        loggedClient!!.on(ReadyEvent::class.java).subscribe { event: ReadyEvent ->
            val self: User = event.self
            F2D.logger.info("Connected as {}#{}", self.username, self.discriminator)
        }
        loggedClient!!.on(MessageCreateEvent::class.java).subscribe { event: MessageCreateEvent ->
            if (event.message.channelId !== F2DConfig.general.ids.getChatChannel()) return@subscribe
            if ((event.message.author.isEmpty || event.message.author.get().isBot) || event.message.webhookId.isPresent) return@subscribe
            if (minecraftServer == null) {
                F2D.logger.error("Attempt to send message with MinecraftServer being null!")
                return@subscribe
            }
            
            val author = event.message.author.get()
            if (event.message.content.isNotEmpty()) {
                // TODO: Find workaround to remove some Placeholder API placeholders
                MessageUtils.sendMinecraftMessage(minecraftServer!!.playerManager, author, TextParserUtils.formatTextSafe(MessageUtils.convertDiscordTags(event.message.content)))
            }
            if (event.message.attachments.isNotEmpty()) {
                MessageUtils.sendMinecraftMessage(minecraftServer!!.playerManager, author, TextParserUtils.formatText(event.message.attachments.joinToString { attachment ->
                    F2DConfig.messages.formattedAttachment.replace("%attachment_url%", attachment.url).replace("%attachment_name%", if (attachment.filename.length > 14) "${attachment.filename.substring(0, 14)}..." else attachment.filename)
                }))
            }
        }
    }
}