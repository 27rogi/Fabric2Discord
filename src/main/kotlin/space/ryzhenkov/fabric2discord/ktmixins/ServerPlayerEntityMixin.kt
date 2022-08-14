package space.ryzhenkov.fabric2discord.ktmixins

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTracker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils

object ServerPlayerEntityMixin {
    fun onDeath(player: ServerPlayerEntity, source: DamageSource, tracker: DamageTracker) {
        if (!ConfigAPI.messages.playerDeath.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() == null) return

        val replacements = hashMapOf<String, String>()
        replacements["death_message"] = tracker.deathMessage.string
        replacements["death_by"] = source.name

        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.playerDeath, player, replacements)
        }
    }

    fun worldChanged(player: ServerPlayerEntity, origin: ServerWorld) {
        if (!ConfigAPI.messages.playerDimension.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() == null) return

        val replacements = hashMapOf<String, String>()
        replacements["world_origin"] = origin.registryKey.value.toString()
        replacements["world_origin_id"] = origin.registryKey.value.path

        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.playerDimension, player, replacements)
        }
    }
}