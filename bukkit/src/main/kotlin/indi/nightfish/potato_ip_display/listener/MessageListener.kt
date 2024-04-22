package indi.nightfish.potato_ip_display.listener

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent


class MessageListener : Listener {
    private val plugin = Bukkit.getPluginManager().getPlugin("PotatoIpDisplay") as PotatoIpDisplay
    private val conf = plugin.conf
    @EventHandler(priority = EventPriority.LOWEST)

    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val playerName = event.player.name
        val ipAttr = IpAttributeMap.playerIpAttributeMap[playerName] ?: "未知"

        val chatmsg = (conf.message.playerChat.string
            .replace("%ipAttr%", ipAttr)
            .replace("%playerName%", "%1\$s")
            .replace("%msg%", "%2\$s"))

        event.format = chatmsg
    }

}