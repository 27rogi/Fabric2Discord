package su.rogi.fabric2discord.config.groups

import dev.kord.common.entity.Snowflake
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class IdSettingsGroup {
    // replace chats with objects that have properties
    // like { id: <channelId>, sources: ["death", "advancement"] }

    var logChannel: Long = 0

    var chatChannel: Long = 0

    var webhook: Long = -1

    fun getWebhook(): Snowflake? {
        return if (webhook.toInt() <= 0) null else Snowflake(webhook)
    }

    fun getChatChannel(): Snowflake? {
        return if (chatChannel.toInt() <= 0) null else Snowflake(chatChannel)
    }

    fun getLogChannel(): Snowflake? {
        return if (logChannel.toInt() <= 0) null else Snowflake(logChannel)
    }
}