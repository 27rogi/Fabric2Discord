package space.ryzhenkov.Fabric2Discord.utils

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.User
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.EmbedData
import discord4j.rest.util.Color
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders.parseText
import eu.pb4.placeholders.api.TextParserUtils
import net.minecraft.network.message.MessageType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import space.ryzhenkov.Fabric2Discord.F2DClient.client
import space.ryzhenkov.Fabric2Discord.config.F2DConfig
import space.ryzhenkov.Fabric2Discord.config.IConfigEmbedMessage
import java.time.Instant
import java.util.*


object MessageUtils {
    var ALLOWED_TAGS = arrayOf("obf", "bold", "underline", "strikethrough", "italic")
    
    fun sendEmbedMessage(channel: Snowflake?, embed: EmbedData) {
        if (channel == null) return
        client.getChannelById(channel).createMessage(embed).block()
    }
    
    fun sendMinecraftMessage(playerManager: PlayerManager, user: User, message: Text) {
        val formattedMessage = format(
            F2DConfig.messages.format.replace("%discord_user%", user.username).replace("%discord_tag%", user.discriminator), playerManager.server
        ).copy().append(message)
    
        playerManager.broadcast(formattedMessage, MessageType.CHAT)
    }
    
    // TODO: Rework this function for a better performance.
    fun getConfigMessage(message: IConfigEmbedMessage, server: MinecraftServer? = null, player: ServerPlayerEntity? = null, customReplacements: HashMap<String, String>? = null): EmbedCreateSpec.Builder {
        val embedMessage = EmbedCreateSpec.builder()
    
        if (message.header.isNotBlank()) {
            if (message.iconHeader.isNotBlank()) embedMessage.author(format(message.header, server, player, customReplacements).string, null, format(message.iconHeader, server, player, customReplacements).string)
            else embedMessage.title(format(message.header, server, player, customReplacements).string)
        }
        if (message.message.isNotBlank()) embedMessage.description(format(message.message, server, player, customReplacements).string)
        if (message.footer.isNotBlank()) embedMessage.footer(format(message.footer, server, player, customReplacements).string, format(message.iconFooter, server, player, customReplacements).string.ifBlank { null })
        if (message.timestamp) embedMessage.timestamp(Instant.now())
        if (message.color.isNotBlank()) embedMessage.color(toColor(message.color))
        if (message.image.isNotBlank()) embedMessage.image(format(message.image, server, player, customReplacements).string)
        
        return embedMessage
    }
    
    fun format(message: String, server: MinecraftServer? = null, player: ServerPlayerEntity? = null, customReplacements: HashMap<String, String>? = null): Text {
        val finalMessage = replacer(message, customReplacements)
        if (player != null) return parseText(TextParserUtils.formatText(finalMessage), PlaceholderContext.of(player))
        if (server != null) return parseText(TextParserUtils.formatText(finalMessage), PlaceholderContext.of(server))
        return TextParserUtils.formatText(finalMessage)
    }
    
    fun format(message: String, server: MinecraftServer?, customReplacements: HashMap<String, String>? = null): Text {
        val finalMessage = replacer(message, customReplacements)
        if (server != null) return parseText(TextParserUtils.formatText(finalMessage), PlaceholderContext.of(server))
        return TextParserUtils.formatText(finalMessage)
    }
    
    fun format(message: String, player: ServerPlayerEntity?, customReplacements: HashMap<String, String>? = null): Text {
        val finalMessage = replacer(message, customReplacements)
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
        return Color.of(Integer.valueOf(finalHex, 16))
    }
    
    fun convertDiscordTags(text: String): String {
        return text.replace(getMarkdownRegex("||", "\\|\\|"), "<obf>$2</obf>").replace(getMarkdownRegex("**", "\\*\\*"), "<bold>$2</bold>").replace(getMarkdownRegex("__", "__"), "<underline>$2</underline>").replace(getMarkdownRegex("~~", "~~"), "<strikethrough>$2</strikethrough>")
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