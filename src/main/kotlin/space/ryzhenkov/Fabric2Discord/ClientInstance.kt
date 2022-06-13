package space.ryzhenkov.Fabric2Discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import eu.pb4.placeholders.TextParser
import net.minecraft.server.MinecraftServer
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils


object ClientInstance {
    
    var client: DiscordClient = DiscordClient.create(ConfigInstance.general.token)
    var loggedClient: GatewayDiscordClient? = client.login().block()
    var minecraftServer: MinecraftServer? = null
    
    fun init() {
        if (loggedClient == null) throw Exception("Discord client is not connected!")
        loggedClient!!.on(ReadyEvent::class.java).subscribe { event: ReadyEvent ->
            val self: User = event.self
            F2D.logger.info("Connected as {}#{}", self.username, self.discriminator)
        }
        loggedClient!!.on(MessageCreateEvent::class.java).subscribe { event: MessageCreateEvent ->
            if ((event.message.author.isEmpty || event.message.author.get().isBot) || event.message.webhookId.isPresent) return@subscribe
            if (minecraftServer == null) {
                F2D.logger.error("Attempt to send message with MinecraftServer being null!")
                return@subscribe
            }
            
            val author = event.message.author.get()
            if (event.message.content.isNotEmpty()) {
                MessageUtils.sendMinecraftMessage(minecraftServer!!.playerManager, author, TextParser.parse(MessageUtils.convertDiscordTags(event.message.content), MessageUtils.ALLOWED_TAGS))
            }
            if (event.message.attachments.isNotEmpty()) {
                MessageUtils.sendMinecraftMessage(minecraftServer!!.playerManager, author, TextParser.parse(event.message.attachments.joinToString { attachment ->
                    ConfigInstance.messages.formattedAttachment.replace("%attachment_url%", attachment.url).replace("%attachment_name%", if (attachment.filename.length > 14) "${attachment.filename.substring(0, 14)}..." else attachment.filename)
                }))
            }
        }
    }
}