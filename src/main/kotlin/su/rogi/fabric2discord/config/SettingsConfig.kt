package su.rogi.fabric2discord.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import su.rogi.fabric2discord.config.groups.IdSettingsGroup
import su.rogi.fabric2discord.config.groups.StatusSettingsGroup

@ConfigSerializable
class SettingsConfig {
    var token: String = ""

    var ids = IdSettingsGroup()
    var status = StatusSettingsGroup()

    var configVersion = 3
}