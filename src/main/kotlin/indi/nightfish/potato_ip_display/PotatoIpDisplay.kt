package indi.nightfish.potato_ip_display

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class PotatoIpDisplay : JavaPlugin() {
    override fun onLoad() {
        super.onLoad()
        logger.info("PotatoIpDisplay loading")
    }

    override fun onEnable() {
        super.onEnable()
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! The placeholders will not work!");
        }
        saveDefaultConfig()
        PotatoPlugin.fileConfig = getConfig()
        logger.info("Registering event -> Listener")
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        server.pluginManager.registerEvents(MessageListener(), this)

    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Disabling PotatoIpDisplay")
    }
}