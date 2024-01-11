package su.rogi.fabric2discord.config.components

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
open class MessageField {
    open var name: String = "name"
    open var value: String = "value"
    open var inline: Boolean? = false
}