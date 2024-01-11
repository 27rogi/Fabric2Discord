package su.rogi.fabric2discord.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import su.rogi.fabric2discord.config.groups.ChatMessagesGroup
import su.rogi.fabric2discord.config.groups.PlayerMessagesGroup
import su.rogi.fabric2discord.config.groups.ServerMessagesGroup

@ConfigSerializable
class MessagesConfig {
    var server = ServerMessagesGroup()
    var chat = ChatMessagesGroup()
    var player = PlayerMessagesGroup()
}