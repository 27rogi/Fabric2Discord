package su.rogi.fabric2discord.mixin

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTracker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.utils.MessageUtils

object ServerPlayerEntityMixinKotlin {
    fun onDeath(player: ServerPlayerEntity, source: DamageSource, tracker: DamageTracker) {
        if (!Configs.MESSAGES.entries.player.died.enabled) return
        if (Configs.SETTINGS.entries.ids.getLogChannel() == null) return

        val replacements = hashMapOf<String, String>()
        replacements["death_message"] = tracker.deathMessage.string
        replacements["death_by"] = source.name

        MessageUtils.sendEmbedMessage(Configs.SETTINGS.entries.ids.getLogChannel()) {
            Configs.MESSAGES.entries.player.died.getEmbed(replacements, player)
        }
    }

    fun worldChanged(player: ServerPlayerEntity, origin: ServerWorld) {
        if (!Configs.MESSAGES.entries.player.teleported.enabled) return
        if (Configs.SETTINGS.entries.ids.getLogChannel() == null) return

        val replacements = hashMapOf<String, String>()
        replacements["world_origin"] = origin.registryKey.value.toString()
        replacements["world_origin_id"] = origin.registryKey.value.path

        MessageUtils.sendEmbedMessage(Configs.SETTINGS.entries.ids.getLogChannel()) {
            Configs.MESSAGES.entries.player.teleported.getEmbed(replacements, player)
        }
    }
}