package su.rogi.fabric2discord.config.groups

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import su.rogi.fabric2discord.config.components.ImageStructure
import su.rogi.fabric2discord.config.components.MessageBase
import su.rogi.fabric2discord.config.components.MessageStructure

@ConfigSerializable
class ChatMessagesGroup {
    @Comment("Supported placeholders: %discord_nickname% (fallback to username if not present), %discord_username%")
    var format = "[<color:4ae485>F2D</color>] %discord_nickname%: "
    var formattedAttachment = "<blue><url:'%attachment_url%'>[%attachment_name%]</url></blue>"

    var message = MessageBase().apply {
        timestamp = true
        text = MessageStructure(
            body = "%message%",
            header = "%player:name%",
        )
        images = ImageStructure(
            header = "https://minotar.net/armor/bust/%player:name%/100.png",
        )
        color = "#01cdfe"
    }
}