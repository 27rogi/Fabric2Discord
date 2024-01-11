package su.rogi.fabric2discord.config

import java.nio.file.Path

object Configs {
    lateinit var SETTINGS: Config<SettingsConfig>
    lateinit var MESSAGES: Config<MessagesConfig>

    fun register() {
        SETTINGS = Config(SettingsConfig::class.java, Path.of("f2d", "settings.conf"))
        MESSAGES = Config(MessagesConfig::class.java, Path.of("f2d", "messages.conf"))

        if (SETTINGS.entries.configVersion != 3) {
         throw Error("""
             Your configuration (v${SETTINGS.entries.configVersion}) is not compatible with this mod version (v3)!
             Visit https://github.com/rogi27/Fabric2Discord/wiki/Configuration for more information.
         """.trimIndent())
        }
    }
}