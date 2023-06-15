package space.ryzhenkov.fabric2discord

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.util.function.Supplier

object CommandHandler {
    fun init() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(CommandManager.literal("f2d")
                .requires { server -> server.hasPermissionLevel(4) }
                .then(CommandManager.literal("reload")
                    .executes { context: CommandContext<ServerCommandSource> ->
                        reload(context)
                    })
            )
        })
    }

    @Throws(CommandSyntaxException::class)
    fun reload(context: CommandContext<ServerCommandSource>): Int {
        try {
            F2D.config.load()
        } catch (err: Error) {
            context.source.sendFeedback(
                { Text.of("There is a problem with current config! (${err.message})").copy() },
                false
            )
        }
        context.source.sendFeedback(
            { Text.of("Configuration files reloaded").copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x19422814))) },
            false
        )
        return 1
    }
}
