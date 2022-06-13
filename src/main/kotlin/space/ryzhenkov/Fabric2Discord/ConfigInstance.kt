package space.ryzhenkov.Fabric2Discord

import discord4j.common.util.Snowflake
import discord4j.core.`object`.presence.Status
import me.lortseam.completeconfig.api.ConfigContainer
import me.lortseam.completeconfig.api.ConfigContainer.Transitive
import me.lortseam.completeconfig.api.ConfigEntries
import me.lortseam.completeconfig.api.ConfigEntry
import me.lortseam.completeconfig.api.ConfigGroup
import space.ryzhenkov.Fabric2Discord.utils.IConfigEmbedMessage

object ConfigInstance:ConfigContainer {
    @Transitive
    object general:ConfigGroup {
        @Override
        override fun getComment(): String {
            return """
            Thanks for using Fabric2Discord!
            The configuration might look very complicated, so I made a simple WIKI to help you:
            https://github.com/rogi27/Fabric2Discord/wiki/Introduction
            
            Since this plugin has a close bound with Placeholder API you might want to check it's WIKI:
            https://placeholders.pb4.eu/
            """.trimIndent()
        }
        
        @ConfigEntry(comment = "Your token should be here, check https://github.com/rogi27/Fabric2Discord/wiki/Getting-Started#connecting-webhook=")
        var token = ""
        
        @Transitive
        object status:ConfigGroup {
            @Override
            override fun getComment(): String {
                return "Here you can enable custom bot statuses"
            }
            
            @ConfigEntry(comment = "Enables or disables this feature")
            var enabled: Boolean = true
    
            @ConfigEntry(comment = "Status type, variants: ONLINE, IDLE, DO_NOT_DISTURB, INVISIBLE, OFFLINE")
            var type: String = "DO_NOT_DISTURB"
    
            @ConfigEntry(comment = "Status update interval in minutes (min = 1, max = 120)")
            @ConfigEntry.BoundedInteger(min = 1, max = 120)
            var interval: Int = 1
    
            @ConfigEntry(
                comment = "Status variants, chosen randomly.\n" + "Supports SERVER placeholders"
            )
            var variants: Array<String> = arrayOf("Fabric", "with %server:online% dudes", "in %world:name%")
        }
        
        @Transitive
        object ids:ConfigGroup {
            @Override
            override fun getComment(): String {
                return "Here you specify ids for mod to work, setting value to 0 disables the feature"
            }
            
            @ConfigEntry(
                comment = "Snowflake (id) of the channel used for join, leave, death and etc. messages\n" + "More on https://github.com/rogi27/Fabric2Discord/wiki/Getting-Started#basic-setup="
            )
            private var logChannel: Long = 0
            
            @ConfigEntry(
                comment = "Snowflake (id) of the channel used to link Discord and Minecraft messages\n" + "More on https://github.com/rogi27/Fabric2Discord/wiki/Getting-Started#basic-setup="
            )
            private var chatChannel: Long = 0
            
            @ConfigEntry(
                comment = "Snowflake (id) of the webhook used to send Minecraft messages using player data\n" + "More on https://github.com/rogi27/Fabric2Discord/wiki/Getting-Started#connecting-webhook="
            )
            private var webhook: Long = 0
            
            fun getWebhookId(): Snowflake? {
                if (webhook <= 0) return null
                return Snowflake.of(webhook)
            }
            
            fun getLogChannel(): Snowflake? {
                if (logChannel <= 0) return null
                return Snowflake.of(logChannel)
            }
            
            fun getChatChannel(): Snowflake? {
                if (chatChannel <= 0) return null
                return Snowflake.of(chatChannel)
            }
        }
    }
    
    @Transitive
    object messages:ConfigGroup {
        @Override
        override fun getComment(): String {
            return """
                You can toggle and customize messages that are being sent
                More on https://github.com/rogi27/Fabric2Discord/wiki/Messages
                """.trimIndent()
        }
        
        @ConfigEntry(
            comment = "Formatting for messages from Discord\n" + "Custom placeholders: %discord_user%, %discord_tag%\n" + "Supports SERVER placeholders"
        )
        var format = "[<color:4ae485>F2D</color>] %discord_user%<gray>#%discord_tag%</gray>: "
        
        @ConfigEntry(
            comment = "Formatting for attachments from Discord (merged with `format`)\n" + "Custom placeholders: %attachment_url%, %attachment_name%\n" + "Supports SERVER placeholders"
        )
        var formattedAttachment = "<blue><url:'%attachment_url%'>[%attachment_name%]</url></blue>"
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object serverStart:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel after server world is loaded
                Supports SERVER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = ":white_check_mark: Server started!"
            override var message: String = "It's %world:time% (day %world:day%) in the world."
            override var footer: String = ""
            override var color: String = "#4ae485"
            override var timestamp: Boolean = true
            override var image: String = "https://source.unsplash.com/600x400/?purple,nature"
            override var iconHeader: String = ""
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object serverStop:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel before shutdown
                Supports SERVER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = ":stop_sign: Server stopped!"
            override var message: String = "You can wait a little for it to start again!"
            override var footer: String = ""
            override var color: String = "#FF2337"
            override var timestamp: Boolean = true
            override var image: String = "https://source.unsplash.com/600x400/?stop"
            override var iconHeader: String = ""
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object playerJoin:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel after player connected
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = "%player:name% has joined"
            override var message: String = ""
            override var footer: String = ""
            override var color: String = "#4ae485"
            override var timestamp: Boolean = false
            override var image: String = ""
            override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object playerLeave:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel after player disconnected
                Custom placeholders: %leave_reason%
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = "%player:name% has left"
            override var message: String = ""
            override var footer: String = "*Time played: %player:playtime%*"
            override var color: String = "#FF2337"
            override var timestamp: Boolean = false
            override var image: String = ""
            override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object playerAdvancement:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel when player gets and advancement
                Custom placeholders: %advancement_name%, %advancement_description%, %advancement_id%
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = "%player:name% got %advancement_name%"
            override var message: String = "%advancement_description%"
            override var footer: String = ""
            override var color: String = "#ffff66"
            override var timestamp: Boolean = true
            override var image: String = "https://source.unsplash.com/600x400/?trophy"
            override var iconHeader: String = ""
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object playerDeath:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel when player dies
                Custom placeholders: %death_by%, %death_message%
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = ":skull: got killed by %death_by%"
            override var message: String = "%death_message%"
            override var footer: String = "You will never be missed!"
            override var color: String = "#696969"
            override var timestamp: Boolean = false
            override var image: String = "https://minotar.net/cube/%player:uuid%/32.png"
            override var iconHeader: String = ""
            override var iconFooter: String = "https://source.unsplash.com/128x128/?death"
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object playerDimension:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to logs channel after player teleported to another dimension
                Custom placeholders: %world_origin%, %world_origin_id%
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = "%player:name% has teleported to %player:world%"
            override var message: String = ""
            override var footer: String = "Previous location is %world_origin%"
            override var color: String = "#b967ff"
            override var timestamp: Boolean = false
            override var image: String = ""
            override var iconHeader: String = "https://minotar.net/cube/%player:name%/100.png"
            override var iconFooter: String = ""
        }
        
        @Transitive
        @ConfigEntries(includeAll = true)
        object chatMessage:ConfigGroup, IConfigEmbedMessage {
            @Override
            override fun getComment(): String {
                return """
                Message that will be sent to chat channel after player sent message in chat
                Custom placeholders: %message%
                Supports PLAYER placeholders
                """.trimIndent()
            }
            
            override var enabled: Boolean = true
            override var header: String = "%player:name%"
            override var message: String = "%message%"
            override var footer: String = ""
            override var color: String = "#01cdfe"
            override var timestamp: Boolean = true
            override var image: String = ""
            override var iconHeader: String = "https://minotar.net/armor/bust/%player:name%/100.png"
            override var iconFooter: String = ""
        }
    }
    
    @ConfigEntry(comment = "Do not touch this value, it allows mod to check if config file is outdated or not.")
    var configVersion = 1
}