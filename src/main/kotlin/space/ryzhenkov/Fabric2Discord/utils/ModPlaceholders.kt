package space.ryzhenkov.Fabric2Discord.utils

import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.util.Identifier


object ModPlaceholders {
    fun init() {
        Placeholders.register(Identifier("player", "uuid")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.uuidAsString)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        Placeholders.register(Identifier("player", "world")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.world.registryKey.value.toString())
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        Placeholders.register(Identifier("player", "world_name")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.world.registryKey.value.path)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
    }
}