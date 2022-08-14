package space.ryzhenkov.fabric2discord.utils

import com.vdurmont.emoji.EmojiParser
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.WebhookMessageCreateBuilder
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders.parseText
import eu.pb4.placeholders.api.TextParserUtils
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import space.ryzhenkov.fabric2discord.F2D
import space.ryzhenkov.fabric2discord.KordInstance
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.config.IConfigEmbed


object MessageUtils {
    fun sendEmbedMessage(channel: Snowflake?, embed: () -> EmbedBuilder) {
        if (channel == null) return
        F2D.scope.launch {
            KordInstance.kord.getChannelOf<TextChannel>(channel)?.createMessage {
                this.embeds.add(0, embed.invoke())
            }
        }
    }

    fun sendMinecraftMessage(playerManager: PlayerManager, user: User, message: () -> Text) {
        val formattedMessage = format(
            ConfigAPI.messages.format.replace("%discord_user%", user.username)
                .replace("%discord_tag%", user.discriminator), playerManager.server
        ).copy().append(message.invoke())

        playerManager.broadcast(formattedMessage, false)
    }

    fun getEmbedMessage(
        message: IConfigEmbed,
        server: MinecraftServer? = null,
        customReplacements: HashMap<String, String>? = null
    ): EmbedBuilder {
        return EmbedBuilder().apply {
            if (message.header.isNotBlank()) {
                if (message.iconHeader.isBlank()) author {
                    name = format(message.header, server, customReplacements).string
                    icon = format(message.iconHeader, server, customReplacements).string
                }
                else title = format(message.header, server, customReplacements).string
            }
            if (message.message.isNotBlank()) description = format(message.message, server, customReplacements).string
            if (message.footer.isNotBlank()) footer {
                text = format(message.footer, server, customReplacements).string
                icon = format(message.iconFooter, server, customReplacements).string.ifBlank { null }
            }
            if (message.timestamp) timestamp = Clock.System.now()
            if (message.color.isNotBlank()) color = toColor(message.color)
            if (message.image.isNotBlank()) image = format(message.image, server, customReplacements).string
        }
    }

    fun getEmbedMessage(
        message: IConfigEmbed,
        player: ServerPlayerEntity? = null,
        customReplacements: HashMap<String, String>? = null
    ): EmbedBuilder {
        return EmbedBuilder().apply {
            if (message.header.isNotBlank()) {
                if (message.iconHeader.isNotBlank()) author {
                    name = format(message.header, player, customReplacements).string
                    icon = format(message.iconHeader, player, customReplacements).string
                }
                else title = format(message.header, player, customReplacements).string
            }
            if (message.message.isNotBlank()) description = format(message.message, player, customReplacements).string
            if (message.footer.isNotBlank()) footer {
                text = format(message.footer, player, customReplacements).string
                icon = format(message.iconFooter, player, customReplacements).string.ifBlank { null }
            }
            if (message.timestamp) timestamp = Clock.System.now()
            if (message.color.isNotBlank()) color = toColor(message.color)
            if (message.image.isNotBlank()) image = format(message.image, player, customReplacements).string
        }
    }

    fun WebhookMessageCreateBuilder.createWebhookMessage(username: String, avatar: String, message: String) {
        this.username = username
        this.avatarUrl = avatar
        this.content = message
    }

    fun format(message: String, server: MinecraftServer?, customReplacements: HashMap<String, String>? = null): Text {
        val finalMessage = EmojiParser.parseToUnicode(replacer(message, customReplacements))
        if (server != null) return parseText(TextParserUtils.formatText(finalMessage), PlaceholderContext.of(server))
        return TextParserUtils.formatText(finalMessage)
    }

    fun format(
        message: String,
        player: ServerPlayerEntity?,
        customReplacements: HashMap<String, String>? = null
    ): Text {
        val finalMessage = EmojiParser.parseToUnicode(replacer(message, customReplacements))
        if (player != null) return parseText(TextParserUtils.formatText(finalMessage), PlaceholderContext.of(player))
        return TextParserUtils.formatText(finalMessage)
    }

    fun replacer(message: String, replacements: HashMap<String, String>? = null): String {
        if (replacements == null) return message
        var newMessage = message
        replacements.forEach { (key, value) -> newMessage = newMessage.replace("%${key}%", value) }
        return newMessage
    }

    fun toColor(hex: String): Color {
        val finalHex = hex.replace("#", "")
        return Color(Integer.valueOf(finalHex, 16))
    }

    fun convertDiscordTags(text: String): String {
        return text
            .replace(getMarkdownRegex("||", "\\|\\|"), "<obf>$2</obf>")
            .replace(getMarkdownRegex("**", "\\*\\*"), "<bold>$2</bold>")
            .replace(getMarkdownRegex("__", "__"), "<underline>$2</underline>")
            .replace(getMarkdownRegex("~~", "~~"), "<strikethrough>$2</strikethrough>")
            .replace(getMarkdownRegex("*", "\\*"), "<italic>$2</italic>")
    }

    /*
     * Parts of the code from StyledChat by Patbox
     * https://github.com/Patbox/StyledChat/blob/e8e792f7ff29b93efab2595fd094415226c8f3d4/src/main/java/eu/pb4/styledchat/StyledChatUtils.java
     */
    private fun getMarkdownRegex(base: String, sides: String): Regex {
        return Regex("($sides)(?<id>[^$base]+)($sides)")
    }
}