package su.rogi.fabric2discord.kord

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.behavior.edit
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
                if (message.channelId != Configs.SETTINGS.entries.ids.getChatChannel()) return@on
                if ((message.author == null || message.author!!.isBot) || message.webhookId != null) return@on
                if (message.content.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(Fabric2Discord.minecraftServer!!.playerManager, message.author!!) {
                        TextParserUtils.formatTextSafe(MessageUtils.convertDiscordTags(message.content))
                    }
                }
                if (message.attachments.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(Fabric2Discord.minecraftServer!!.playerManager, message.author!!) {
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
        when (Configs.SETTINGS.entries.ids.webhook.toInt()) {
            0 -> {
                Fabric2Discord.logger.info("Webhook is disabled by configuration file")
            }
            -1 -> {
                Fabric2Discord.logger.warn("Your webhook value is -1, creating new webhook for chat #${Configs.SETTINGS.entries.ids.getChatChannel()}...")
                if (Configs.SETTINGS.entries.ids.getChatChannel() != null) {
                    runBlocking {
                        val webhook =
                            kord.getChannelOf<TextChannel>(Configs.SETTINGS.entries.ids.getChatChannel()!!)!!
                                .createWebhook("F2DHook")
                        Configs.SETTINGS.entries.ids.webhook = webhook.id.value.toLong()
                        Configs.SETTINGS.save().load()
                    }
                } else {
                    Fabric2Discord.logger.warn("Could not create webhook due to `chatChannel` being disabled. Setting webhook value as 0...")
                    Configs.SETTINGS.entries.ids.webhook = 0
                    Configs.SETTINGS.save().load()
                }
            }
            else -> {
                if (Configs.SETTINGS.entries.ids.getChatChannel() != null) {
                    runBlocking {
                        val webhook = kord.getWebhook(Configs.SETTINGS.entries.ids.getWebhook()!!)
                        if (webhook.channel.id != Configs.SETTINGS.entries.ids.getChatChannel()!!) {
                            Fabric2Discord.logger.info("Webhook channel doesn't match one specified in config, updating...")
                            webhook.edit { channelId = Configs.SETTINGS.entries.ids.getChatChannel()!! }
                        }
                    }
                }
                Fabric2Discord.logger.info("Using webhook with id `${Configs.SETTINGS.entries.ids.webhook}`")
            }
        }
    }

    fun executeWebhook(username: String, avatar: String, message: String) {
        if (Configs.SETTINGS.entries.ids.getWebhook() === null) {
            Fabric2Discord.logger.warn("Unable to execute webhook because its value is null")
            return
        }
        Fabric2Discord.scope.launch {
            val webhook = kord.getWebhook(Configs.SETTINGS.entries.ids.getWebhook()!!)
            webhook.execute(webhook.token!!, null) { createWebhookMessage(username, avatar, message) }
        }
    }

    fun stop() {
        // TODO: fix throwable JobCancellationException
        kordListener.cancel()
        runBlocking { kord.logout() }
    }
}