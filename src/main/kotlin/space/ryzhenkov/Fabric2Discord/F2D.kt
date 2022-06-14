package space.ryzhenkov.Fabric2Discord


import me.lortseam.completeconfig.data.Config
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import space.ryzhenkov.Fabric2Discord.config.F2DConfig
import space.ryzhenkov.Fabric2Discord.utils.PlaceholderUtils

class F2D:ModInitializer {
    override fun onInitialize() {
        config.load()
        if (F2DConfig.general.token.isEmpty()) throw Error("You must provide Discord API token for this mod to work!")
        
        F2DCommand.init()
        PlaceholderUtils.init()
        
        try {
            F2DClient.init()
        } catch (err: ExceptionInInitializerError) {
            throw Error("Unable to connect to Discord API with provided token!")
        }
    }
    
    companion object {
        @JvmField
        var logger: Logger = LogManager.getLogger("Fabric2Discord")
        val config: Config = Config("f2d", arrayOf("settings"), F2DConfig)
    }
}

