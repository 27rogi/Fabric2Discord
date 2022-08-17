package space.ryzhenkov.fabric2discord.utils

import eu.pb4.placeholders.PlaceholderAPI
import eu.pb4.placeholders.PlaceholderResult
import net.minecraft.util.Identifier


object PlaceholderUtils {
    fun init() {
        PlaceholderAPI.register(Identifier("player", "uuid")) { handler ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player.uuidAsString)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        PlaceholderAPI.register(Identifier("player", "world")) { handler ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player.world.registryKey.value.toString())
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        PlaceholderAPI.register(Identifier("player", "world_name")) { handler ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player.world.registryKey.value.path)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
    }
}