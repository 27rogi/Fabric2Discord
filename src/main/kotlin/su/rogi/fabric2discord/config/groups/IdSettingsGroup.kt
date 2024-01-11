package su.rogi.fabric2discord.config.groups

import dev.kord.common.entity.Snowflake
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import su.rogi.fabric2discord.config.components.ChannelCategory

@ConfigSerializable
class IdSettingsGroup {
    @Comment(
        "List of channels with broadcast categories\n" +
        "Available categories: TELEPORTS, DEATHS, ADVANCEMENTS, CONNECTIONS, SERVER_STATUS, GAME_CHAT, SERVER_CHAT\n" +
        "Example: 1058047687309664276: [CHAT, SERVER_STATUS]"
    )
    var channels: HashMap<Long, Array<ChannelCategory>> = hashMapOf()

    var webhooks: Array<Long>? = arrayOf()

    fun getByCategories(categories: List<ChannelCategory>): List<Snowflake>? {
        if (channels.isEmpty()) return null
        val filtered = channels.filter {
            val intersect = categories intersect it.value.toSet()
            return@filter intersect.isNotEmpty()
        }
        if (filtered.isEmpty()) return null
        return filtered.keys.map { Snowflake(it) }
    }

    fun getByCategory(category: ChannelCategory): List<Snowflake>? {
        if (channels.isEmpty()) return null
        val filtered = channels.filter { it.value.contains(category) }
        if (filtered.isEmpty()) return null
        return filtered.keys.map { Snowflake(it) }
    }

    fun getWebhooks(): List<Snowflake>? {
        return webhooks?.map { Snowflake(it) }
    }
}