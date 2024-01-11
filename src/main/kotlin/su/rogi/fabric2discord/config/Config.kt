package su.rogi.fabric2discord.config

import net.fabricmc.loader.api.FabricLoader
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import su.rogi.fabric2discord.Fabric2Discord
import java.nio.file.Path
import kotlin.system.exitProcess

open class Config<V>(
    private val type: Class<V>,
    private val path: Path,
    private val header: String? = null
) {
    private var loader: HoconConfigurationLoader = HoconConfigurationLoader.builder()
        .path(FabricLoader.getInstance().configDir.resolve(path))
        .defaultOptions { it.header(header) }
        .build();
    private var node: CommentedConfigurationNode = loader.load();
    var entries: V = checkNotNull(node[type]) {
        "Unable to get configuration node for $type"
    }

    init {
        println("init")
        save()
    }

    fun load(): Config<V> {
        println("load ${loader.canLoad()}")
        try {
            node = loader.load()
            entries = node[type]!!
        } catch (e: Exception) {
            Fabric2Discord.logger.error("An error occurred while loading configuration for ${this::class.java.name}: ${e.message}")
            if (e.cause != null) {
                e.cause!!.printStackTrace()
            }
            exitProcess(1)
        }
        return this
    }

    fun save(): Config<V> {
        println("save")
        try {
            node[type] = entries
            loader.save(node)
        } catch (e: Exception) {
            Fabric2Discord.logger.error("An error occurred while saving configuration for ${this::class.java.name}: ${e.message}")
            if (e.cause != null) {
                e.cause!!.printStackTrace()
            }
            exitProcess(1)
        }
        return this
    }
}