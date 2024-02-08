package su.rogi.fabric2discord.config.components

import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.datetime.Clock
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import su.rogi.fabric2discord.utils.MessageUtils
import su.rogi.fabric2discord.utils.MessageUtils.format

@ConfigSerializable
open class MessageBase {
    open var enabled: Boolean = true
    open var silent: Boolean = false
    open var timestamp: Boolean = false
    open var fields: Array<MessageField> = arrayOf()

    var url: String? = null
    var color: String? = null
    var text: MessageStructure = MessageStructure()
    var images: ImageStructure? = null

    fun getEmbed(
        tags: HashMap<String, String>? = null,
        player: ServerPlayerEntity? = null,
        server: MinecraftServer? = null
    ): EmbedBuilder {
        return EmbedBuilder().apply {
            if (this@MessageBase.color != null) {
                this.color = MessageUtils.toColor(this@MessageBase.color!!)
            }
            this.timestamp = if (this@MessageBase.timestamp) Clock.System.now() else null
            if (this@MessageBase.text.header !== null) {
                author {
                    name = format(this@MessageBase.text.header, tags, server, player)?.string
                    icon = format(this@MessageBase.images?.header, tags, server, player)?.string
                    url = format(this@MessageBase.url, tags, server, player)?.string
                }
            } else this.url = this@MessageBase.url
            this.description = format(text.body, tags, server, player)?.string
            if (this@MessageBase.fields.isNotEmpty()) {
                for (field in this@MessageBase.fields) {
                    this.field {
                        this.name = checkNotNull(format(field.name, tags, server, player)?.string)
                        this.value = checkNotNull(format(field.value, tags, server, player)?.string)
                        this.inline = field.inline
                    }
                }
            }
            if (this@MessageBase.text.footer != null) {
                this@apply.footer {
                    this.text = format(this@MessageBase.text.footer, tags, server, player)?.string!!
                    this.icon = format(this@MessageBase.images?.footer, tags, server, player)?.string
                }
            }
            this.image = this@MessageBase.images?.image
            if (this@MessageBase.images?.thumbnail != null) {
                this.thumbnail {
                    this@thumbnail.url = format(this@MessageBase.images?.thumbnail!!, tags, server, player)?.string!!
                }
            }
        }
    }
}