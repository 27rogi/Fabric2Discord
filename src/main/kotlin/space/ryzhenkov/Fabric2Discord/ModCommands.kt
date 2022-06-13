package space.ryzhenkov.Fabric2Discord

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import discord4j.rest.util.Color
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text

object ModCommands {
    fun init() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(CommandManager.literal("f2d")
                    .requires { server -> server.hasPermissionLevel(4) }
                    .then(CommandManager.literal("reload")
                            .executes { context: CommandContext<ServerCommandSource> ->
                                reload(context)
                            }))
        })
    }
    
    @Throws(CommandSyntaxException::class)
    fun reload(context: CommandContext<ServerCommandSource>): Int {
        try {
            F2D.config.load()
        } catch (err: Error) {
            context.source.sendFeedback(Text.of("There is a problem with current config! (${err.message})").copy().setStyle(Style.EMPTY.withColor(Color.RUST.rgb)), false)
        }
        context.source.sendFeedback(Text.of("Config reloaded!").copy().setStyle(Style.EMPTY.withColor(Color.DEEP_SEA.rgb)), false)
        return 1
    }
}
