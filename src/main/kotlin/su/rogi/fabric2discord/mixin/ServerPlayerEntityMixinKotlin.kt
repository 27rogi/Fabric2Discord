package su.rogi.fabric2discord.mixin

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTracker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.utils.MessageUtils

object ServerPlayerEntityMixinKotlin {
    fun onDeath(player: ServerPlayerEntity, source: DamageSource, tracker: DamageTracker) {
        if (!Configs.MESSAGES.entries.player.died.enabled) return

        val replacements = hashMapOf<String, String>()
        replacements["death_message"] = tracker.deathMessage.string
        replacements["death_by"] = source.name

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.DEATHS)) {
            Configs.MESSAGES.entries.player.died.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(replacements, player))
            }
        }
    }

    fun worldChanged(player: ServerPlayerEntity, origin: ServerWorld) {
        if (!Configs.MESSAGES.entries.player.teleported.enabled) return

        val replacements = hashMapOf<String, String>()
        replacements["world_origin"] = origin.registryKey.value.toString()
        replacements["world_origin_id"] = origin.registryKey.value.path

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.TELEPORTS)) {
            Configs.MESSAGES.entries.player.teleported.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(replacements, player))
            }
        }
    }
}