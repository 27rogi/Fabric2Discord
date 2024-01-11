package su.rogi.fabric2discord.config.groups

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import su.rogi.fabric2discord.config.components.ImageStructure
import su.rogi.fabric2discord.config.components.MessageBase
import su.rogi.fabric2discord.config.components.MessageStructure

@ConfigSerializable
class PlayerMessagesGroup {
    var joined = MessageBase().apply {
        text = MessageStructure(
            header = "%player:name% has joined",
        )
        images = ImageStructure(
            header = "https://minotar.net/cube/%player:name%/100.png",
        )
        color = "#4ae485"
    }

    var left = MessageBase().apply {
        text = MessageStructure(
            header = "%player:name% has left",
            footer = "Time played: %player:playtime%"
        )
        images = ImageStructure(
            header = "https://minotar.net/cube/%player:name%/100.png",
        )
        color = "#FF2337"
    }

    // TODO: find a better workaround to include special properties
    @ConfigSerializable
    class AdvancementMessageBase : MessageBase() {
        var ignoresGamerule: Boolean = true
    }

    var gotAdvancement = AdvancementMessageBase().apply {
        text = MessageStructure(
            header = "%player:name% got %advancement_name%",
            body = "%advancement_description%"
        )
        color = "#ffff66"
    }

    var died = MessageBase().apply {
        timestamp = true
        text = MessageStructure(
            body = ":skull: got killed by %death_by%",
            footer = "%death_message%"
        )
        images = ImageStructure(
            image = "https://minotar.net/cube/%player:name%/100.png",
            thumbnail = "https://minotar.net/cube/%player:uuid%/32.png"
        )
        color = "#696969"
    }

    var teleported = MessageBase().apply {
        timestamp = true
        text = MessageStructure(
            body = "%player:name% has teleported to %player:world%",
            footer = "Previous location is %world_origin%"
        )
        images = ImageStructure(
            thumbnail = "https://minotar.net/cube/%player:uuid%/32.png"
        )
        color = "#b967ff"
    }
}