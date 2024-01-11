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
//    @ConfigSerializable
//    object joined : ConfigGroup, IConfigEmbed {
//        override var enabled: Boolean = true
//        override var header: String = "%player:name% has joined"
//        override var message: String = ""
//        override var footer: String = ""
//        override var color: String = "#4ae485"
//        override var timestamp: Boolean = false
//        override var image: String = ""
//        override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
//        override var iconFooter: String = ""
//    }
//
//    @ConfigSerializable
//    object left : ConfigGroup, IConfigEmbed {
//        override var enabled: Boolean = true
//        override var header: String = "%player:name% has left"
//        override var message: String = ""
//        override var footer: String = "Time played: %player:playtime%"
//        override var color: String = "#FF2337"
//        override var timestamp: Boolean = false
//        override var image: String = ""
//        override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
//        override var iconFooter: String = ""
//    }
//
//    @ConfigSerializable
//    object gotAdvancement : ConfigGroup, IConfigEmbed {
//        override var enabled: Boolean = true
//        override var header: String = "%player:name% got %advancement_name%"
//        override var message: String = "%advancement_description%"
//        override var footer: String = ""
//        override var color: String = "#ffff66"
//        override var timestamp: Boolean = true
//        override var image: String = "https://source.unsplash.com/600x400/?trophy"
//        override var iconHeader: String = ""
//        override var iconFooter: String = ""
//        var ignoreGamerule: Boolean = true
//    }
//
//    @ConfigSerializable
//    object died : ConfigGroup, IConfigEmbed {
//        override var enabled: Boolean = true
//        override var header: String = ":skull: got killed by %death_by%"
//        override var message: String = "%death_message%"
//        override var footer: String = "You will never be missed!"
//        override var color: String = "#696969"
//        override var timestamp: Boolean = false
//        override var image: String = "https://minotar.net/cube/%player:uuid%/32.png"
//        override var iconHeader: String = ""
//        override var iconFooter: String = "https://source.unsplash.com/128x128/?death"
//    }
//
//    @ConfigSerializable
//    object teleported : ConfigGroup, IConfigEmbed {
//        override var enabled: Boolean = true
//        override var header: String = "%player:name% has teleported to %player:world%"
//        override var message: String = ""
//        override var footer: String = "Previous location is %world_origin%"
//        override var color: String = "#b967ff"
//        override var timestamp: Boolean = false
//        override var image: String = ""
//        override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
//        override var iconFooter: String = ""
//    }
}