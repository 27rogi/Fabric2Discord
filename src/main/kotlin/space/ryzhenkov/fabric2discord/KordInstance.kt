package space.ryzhenkov.fabric2discord

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.on
import eu.pb4.placeholders.TextParser
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils
import space.ryzhenkov.fabric2discord.utils.MessageUtils.createWebhookMessage


object KordInstance {

    lateinit var kord: Kord

    fun launch() {
        runBlocking {
            try {
                kord = Kord(ConfigAPI.general.token)
            } catch (err: KordInitializationException) {
                throw Error("Unable to connect to Discord API with provided token!")
            }
        }
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch {
            kord.on<ReadyEvent> {
                F2D.logger.info("Connected as {}#{}", self.username, self.discriminator)
            }
            kord.on<MessageCreateEvent> {
                if (F2D.minecraftServer == null) {
                    F2D.logger.warn("Unable to send chat message because server is not loaded yet!")
                    return@on
                }
                if (message.channelId != ConfigAPI.general.ids.getChatChannelOrNull()) return@on
                if ((message.author == null || message.author!!.isBot) || message.webhookId != null) return@on
                if (message.content.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(F2D.minecraftServer!!.playerManager, message.author!!) {
                        TextParser.parse(MessageUtils.convertDiscordTags(message.content), )
                    }
                }
                if (message.attachments.isNotEmpty()) {
                    MessageUtils.sendMinecraftMessage(F2D.minecraftServer!!.playerManager, message.author!!) {
                        TextParser.parse(message.attachments.joinToString { attachment ->
                            ConfigAPI.messages.formattedAttachment.replace("%attachment_url%", attachment.url).replace(
                                "%attachment_name%",
                                if (attachment.filename.length > 14) "${
                                    attachment.filename.substring(
                                        0,
                                        14
                                    )
                                }..." else attachment.filename
                            )
                        })
                    }
                }
            }
            kord.login() {
                intents += Intent.GuildMessages
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    fun executeWebhook(username: String, avatar: String, message: String) {
        if (ConfigAPI.general.ids.getWebhookOrNull() == null) {
            F2D.logger.warn("Unable to execute webhook because its value is null")
            return
        }
        F2D.scope.launch {
            val webhook = kord.getWebhook(ConfigAPI.general.ids.getWebhookOrNull()!!)
            webhook.execute(webhook.token!!, null) { createWebhookMessage(username, avatar, message) }
        }
    }
}