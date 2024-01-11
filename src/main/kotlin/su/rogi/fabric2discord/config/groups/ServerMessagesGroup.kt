package su.rogi.fabric2discord.config.groups

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import su.rogi.fabric2discord.config.components.ImageStructure
import su.rogi.fabric2discord.config.components.MessageBase
import su.rogi.fabric2discord.config.components.MessageStructure

@ConfigSerializable
class ServerMessagesGroup {
    var started = MessageBase().apply {
        enabled = true
        timestamp = true
        text = MessageStructure(
            body = "It's %world:time% (day %world:day%) in the world.",
            header = ":white_check_mark: Server started!",
        )
        images = ImageStructure(
            image = "https://source.unsplash.com/600x400/?purple,nature",
        )
        color = "#4ae485"
    }

    var stopped = MessageBase().apply {
        enabled = true
        timestamp = true
        text = MessageStructure(
            body = ":stop_sign: Server stopped!",
            footer = "You can wait a little for it to start again!",
        )
        images = ImageStructure(
            image = "https://source.unsplash.com/600x400/?stop",
        )
        color = "#FF2337"
    }
}