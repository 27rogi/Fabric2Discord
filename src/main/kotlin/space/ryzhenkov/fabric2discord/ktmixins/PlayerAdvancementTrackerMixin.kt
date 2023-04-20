package space.ryzhenkov.fabric2discord.ktmixins

import net.minecraft.advancement.Advancement
import net.minecraft.server.network.ServerPlayerEntity
import space.ryzhenkov.fabric2discord.config.ConfigAPI
import space.ryzhenkov.fabric2discord.utils.MessageUtils

object PlayerAdvancementTrackerMixin {
    fun grantCriterion(owner: ServerPlayerEntity, advancement: Advancement, isGameruleEnabled: Boolean) {
        if (!ConfigAPI.messages.playerAdvancement.enabled) return
        if (ConfigAPI.general.ids.getLogChannelOrNull() == null) return
        if (!isGameruleEnabled && !ConfigAPI.messages.playerAdvancement.ignoreGamerule) return

        val replacements = hashMapOf<String, String>()
        if (advancement.display != null) {
            replacements["advancement_name"] = advancement.display!!.title.string
            replacements["advancement_description"] = advancement.display!!.description.string
            replacements["advancement_id"] = advancement.id.path.toString()
        }

        MessageUtils.sendEmbedMessage(ConfigAPI.general.ids.getLogChannelOrNull()) {
            MessageUtils.getEmbedMessage(ConfigAPI.messages.playerAdvancement, owner, replacements)
        }
    }
}