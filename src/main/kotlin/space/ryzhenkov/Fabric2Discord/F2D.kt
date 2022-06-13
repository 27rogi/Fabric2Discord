package space.ryzhenkov.Fabric2Discord


import me.lortseam.completeconfig.data.Config
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import space.ryzhenkov.Fabric2Discord.utils.ModPlaceholders

class F2D:ModInitializer {
    override fun onInitialize() {
        config.load()
        if (ConfigInstance.general.token.isEmpty()) throw Error("You must provide Discord API token for this mod to work!")
        
        ModCommands.init()
        ModPlaceholders.init()
        
        try {
            ClientInstance.init()
        } catch (err: ExceptionInInitializerError) {
            throw Error("Unable to connect to Discord API with provided token!")
        }
    }
    
    companion object {
        @JvmField
        var logger: Logger = LogManager.getLogger("Fabric2Discord")
        val config: Config = Config("f2d", arrayOf("settings"), ConfigInstance)
    }
}

