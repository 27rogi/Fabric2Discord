package su.rogi.fabric2discord.utils

import com.vdurmont.emoji.EmojiParser
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.WebhookMessageCreateBuilder
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders.parseText
import eu.pb4.placeholders.api.TextParserUtils.formatText
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import su.rogi.fabric2discord.Fabric2Discord
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.kord.KordClient

object MessageUtils {
    fun sendDiscordMessage(channels: List<Snowflake>?, builder: UserMessageCreateBuilder.() -> Unit) {
        if (channels.isNullOrEmpty()) return
        Fabric2Discord.scope.launch {
            for (channel in channels) {
                KordClient.kord.getChannelOf<TextChannel>(channel)!!.createMessage {
                    builder()
                }
            }
        }
    }

    fun sendMinecraftMessage(playerManager: PlayerManager, member: Member, message: () -> Text) {
        val replacements = hashMapOf(
            Pair("discord_username", member.username),
            Pair("discord_nickname", member.effectiveName),
            // TODO: implement colorful names for chat depending on role or profile customization
            // Pair("discord_color", member.accentColor)
        )
        val formattedMessage = checkNotNull(format(
            Configs.MESSAGES.entries.chat.format,
            tags = replacements,
            server = playerManager.server
        )).copy().append(message.invoke())

        playerManager.broadcast(formattedMessage, false)
    }

    fun WebhookMessageCreateBuilder.createWebhookMessage(username: String, avatar: String, message: String) {
        this.username = username
        this.avatarUrl = avatar
        this.content = message
    }

    fun format(message: String?, tags: HashMap<String, String>? = null, server: MinecraftServer? = null, player: ServerPlayerEntity? = null): Text? {
        if (message == null) return null
        val finalMessage = EmojiParser.parseToUnicode(replacer(message, tags))
        if (server != null && player != null)
            return parseText(formatText(finalMessage), PlaceholderContext.of(player.gameProfile, server))
        if (server != null)
            return parseText(formatText(finalMessage), PlaceholderContext.of(server))
        if (player != null)
            return parseText(formatText(finalMessage), PlaceholderContext.of(player))
        return formatText(finalMessage)
    }

    private fun replacer(message: String, replacements: HashMap<String, String>? = null): String {
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