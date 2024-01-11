package su.rogi.fabric2discord.config.components

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class MessageStructure (
    var body: String? = null,
    var header: String? = null,
    var footer: String? = null
)