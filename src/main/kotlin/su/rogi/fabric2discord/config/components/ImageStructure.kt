package su.rogi.fabric2discord.config.components

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class ImageStructure (
    var thumbnail: String? = null,
    var image: String? = null,
    var header: String? = null,
    var footer: String? = null
)