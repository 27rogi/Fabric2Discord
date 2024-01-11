package su.rogi.fabric2discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.kord.KordClient
import su.rogi.fabric2discord.utils.PlaceholderUtils

class Fabric2Discord : ModInitializer {

    override fun onInitialize() {
        logger.info("Fabric2Discord ${metadata.version} by ${metadata.authors.joinToString(",") { it.name }}")
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            logger.warn("This mod was developed for server-side use and may cause unexpected behaviour on client.")
        }

        PlaceholderUtils.registerPlaceholders()
        Configs.register()
        Commands.register()
        KordClient.create()
        KordClient.registerWebhook()
    }

    companion object {
        var minecraftServer: MinecraftServer? = null
        val scope = CoroutineScope(Dispatchers.IO)
        val metadata: ModMetadata = FabricLoader.getInstance().getModContainer("f2d").get().metadata
        val logger: Logger = LogManager.getLogger(metadata.name)
    }
}