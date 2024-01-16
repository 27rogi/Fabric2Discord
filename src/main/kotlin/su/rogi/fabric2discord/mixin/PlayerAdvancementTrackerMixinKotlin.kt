package su.rogi.fabric2discord.mixin

import net.minecraft.advancement.Advancement
import net.minecraft.server.network.ServerPlayerEntity
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.utils.MessageUtils

object PlayerAdvancementTrackerMixinKotlin {
    fun grantCriterion(owner: ServerPlayerEntity, advancement: Advancement, isGameruleEnabled: Boolean) {
        if (!Configs.MESSAGES.entries.player.gotAdvancement.enabled) return
        if (!isGameruleEnabled && !Configs.MESSAGES.entries.player.gotAdvancement.ignoresGamerule) return

        val replacements = hashMapOf<String, String>()
        if (advancement.display != null) {
            val display = advancement.display!!
            replacements["advancement_name"] = display.title.string
            replacements["advancement_description"] = display.description.string
            replacements["advancement_id"] = advancement.id.path
        }

        MessageUtils.sendEmbedMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.ADVANCEMENTS)) {
            Configs.MESSAGES.entries.player.gotAdvancement.getEmbed(replacements, owner)
        }
    }
}