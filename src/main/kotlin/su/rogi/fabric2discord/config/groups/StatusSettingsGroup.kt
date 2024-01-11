package su.rogi.fabric2discord.config.groups

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class StatusSettingsGroup {
    var enabled: Boolean = true

    var type: String = "DO_NOT_DISTURB"

    var action: String = "STREAMING"

    var url: String = "minecraft.net"

    var interval: Int = 1

    var variants: Array<String> = arrayOf("Fabric", "with %server:online% dudes", "in %world:name%")
}