package su.rogi.fabric2discord.kord

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.behavior.execute
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import eu.pb4.placeholders.api.TextParserUtils
import kotlinx.coroutines.*
import su.rogi.fabric2discord.Fabric2Discord
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.utils.MessageUtils
import su.rogi.fabric2discord.utils.MessageUtils.createWebhookMessage
import kotlin.system.exitProcess

object KordClient {

    lateinit var kord: Kord
    private lateinit var kordListener: Job

    fun create() {
        runBlocking {
            try {
                kord = Kord(Configs.SETTINGS.entries.token)
            } catch (err: KordInitializationException) {
                Fabric2Discord.logger.error("""
                    Unable to start the bot, make sure you are using valid token!
                    
                    You can visit documentation to get help:
                    https://github.com/27rogi/Fabric2Discord/wiki/Getting-Started
                    
                    If error still appears please create an issue here:
                    https://github.com/27rogi/Fabric2Discord/issues
                    
                    Error: {}
                """.trimIndent(), err.stackTraceToString())
                exitProcess(1)
            }
        }
        @OptIn(DelicateCoroutinesApi::class)
        kordListener = GlobalScope.launch {
            kord.on<ReadyEvent> {
                Fabric2Discord.logger.info("Connected as {}", self.username)
            }
            kord.on<MessageCreateEvent> {
                if (Fabric2Discord.minecraftServer == null) {
                    Fabric2Discord.logger.warn("Unable to send chat message because server is not loaded yet!")
                    return@on
                }
                if (Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.SERVER_CHAT)?.contains(message.channelId) != true) return@on
                if ((message.author == null || message.author!!.isBot) || message.webhookId != null) return@on
                if (this.guildId == null) return@on Fabric2Discord.logger.error("Unable to get guildId for incoming message!")
                if (message.content.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(Fabric2Discord.minecraftServer!!.playerManager, message.author!!.asMember(this.guildId!!)) {
                        TextParserUtils.formatTextSafe(MessageUtils.convertDiscordTags(message.content))
                    }
                }
                if (message.attachments.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(Fabric2Discord.minecraftServer!!.playerManager, message.author!!.asMember(this.guildId!!)) {
                        TextParserUtils.formatText(message.attachments.joinToString { attachment ->
                            Configs.MESSAGES.entries.chat.formattedAttachment.replace("%attachment_url%", attachment.url).replace(
                                "%attachment_name%",
                                if (attachment.filename.length > 14)
                                    "${attachment.filename.substring(0, 14)}..."
                                else
                                    attachment.filename
                            )
                        })
                    }
                }
            }
            kord.login {
                intents += Intent.GuildMessages
                intents += Intent.GuildWebhooks
                intents += Intent.GuildIntegrations
                @OptIn(PrivilegedIntent::class)
                intents += Intent.GuildPresences
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    fun registerWebhook() {
        if (Configs.SETTINGS.entries.ids.webhooks == null) {
            Fabric2Discord.logger.info("Webhooks are disabled because entry is null")
            return
        }
        if (Configs.SETTINGS.entries.ids.webhooks!!.isEmpty()) {
            val chats = Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.GAME_CHAT)
            if (chats.isNullOrEmpty()) {
                Fabric2Discord.logger.info("Unable to create webhooks because there are no channels with `GAME_CHAT` category")
                return
            }
            for (chat in chats) {
                runBlocking {
                    val webhook = kord.getChannelOf<TextChannel>(chat)?.createWebhook("F2DHook")
                    if (webhook == null) {
                        Fabric2Discord.logger.warn("Invalid channel id for $chat")
                        return@runBlocking
                    }
                    Configs.SETTINGS.entries.ids.webhooks = Configs.SETTINGS.entries.ids.webhooks!!.plus(webhook.id.value.toLong())
                    Configs.SETTINGS.save().load()
                }
            }
            return
        }
    }

    fun executeWebhook(username: String, avatar: String, message: String) {
        if (Configs.SETTINGS.entries.ids.getWebhooks().isNullOrEmpty()) {
            Fabric2Discord.logger.warn("Unable to execute webhooks because they are empty or null")
            return
        }
        Fabric2Discord.scope.launch {
            for (webhookId in Configs.SETTINGS.entries.ids.getWebhooks()!!) {
                val webhook = kord.getWebhook(webhookId)
                webhook.execute(webhook.token!!, null) { createWebhookMessage(username, avatar, message) }
            }
        }
    }

    fun stop() {
        // TODO: fix throwable JobCancellationException
        kordListener.cancel()
        runBlocking { kord.logout() }
    }
}