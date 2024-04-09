package indi.nightfish.potato_ip_display

import indi.nightfish.potato_ip_display.integration.PlaceholderIntergration
import indi.nightfish.potato_ip_display.listener.MessageListener
import indi.nightfish.potato_ip_display.listener.PlayerJoinListener
import indi.nightfish.potato_ip_display.util.Config
import indi.nightfish.potato_ip_display.util.UpdateUtil
import indi.nightfish.potato_ip_display.util.loadConfig
import me.clip.placeholderapi.metrics.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class PotatoIpDisplay : JavaPlugin() {
    lateinit var conf: Config

    override fun onLoad() {
        super.onLoad()
        reloadConfig()
    }

    override fun onEnable() {
        super.onEnable()
        val pm = Bukkit.getPluginManager()
        conf = loadConfig(config)

        initResources()

        if (conf.papi.enabled) {
            if (pm.getPlugin("PlaceholderAPI") != null) {
                PlaceholderIntergration().register()
            } else {
                throw RuntimeException("PlaceholderAPI enabled in config but NOT installed!")
            }
        }

        /* Registering events" */
        if (conf.message.playerChat.enabled) {
            // NOTE: formerly "parseOnly", but now we determine if PIPD listens for messages depending on it
            pm.registerEvents(MessageListener(), this)
        }
        pm.registerEvents(PlayerJoinListener(), this)

        /* Registering commands */
        /*getCommand("potatoipdisplay")!!.setExecutor(PotatoIpDisplayCommand())
        getCommand("pipd")!!.setExecutor(PotatoIpDisplayCommand())*/

        if (conf.options.allowbStats) {
            Metrics(this, 21473)
        }
        log("PotatoIpDisplay has been enabled. [mode: ${conf.options.mode}]")
    }

    override fun onDisable() {
        super.onDisable()
        log("Disabled")
    }

    private fun initResources() {
        val configFile = File(dataFolder, "config.yml")
        val dbFile = File(dataFolder, "ip2region.xdb")

        if(!configFile.exists()) {
            this.saveDefaultConfig()
        }
        val isLite = false
        if (conf.options.mode == "ip2region" && !dbFile.exists()) {
            if(isLite) {
                UpdateUtil.downloadDatabase(
                    "https://raw.githubusercontent.com/lionsoul2014/ip2region/master/data/ip2region.xdb", dbFile.toPath())
                // TODO: Download db files from internet for lite builds
            } else { /* For non-lite builds, ip2region.xdb included in jar */
                saveResource("ip2region.xdb", false)
                log("ip2region.xdb saved to plugin directory.")
            }
        }
    }

    fun log(message: String, level: Level = Level.INFO) =
        logger.log(level, message)
}