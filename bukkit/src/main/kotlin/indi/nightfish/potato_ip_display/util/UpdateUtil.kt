package indi.nightfish.potato_ip_display.util

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import org.bukkit.Bukkit
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.util.logging.Level
import kotlin.math.min

object UpdateUtil {
    private val httpClient = HttpClient.newHttpClient()
    private val plugin = Bukkit.getPluginManager().getPlugin("PotatoIpDisplay") as PotatoIpDisplay


    fun downloadDatabase(url: String, path: Path) {
        plugin.log("Start download database file")
        Thread {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build()
                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(path))

                if (response.statusCode() != 200) {
                    throw IOException("Download database file FAILED: ${response.statusCode()}")
                }

                plugin.log("Successful downloaded to $path")
            } catch (e: Exception) {
                plugin.log("Download Failed! Please manually download from [$url],\n" +
                        "then move it to the plugin directory! (plugins\\PotatoIpDisplay\\{FILE})", Level.WARNING)
                /*e.printStackTrace()*/
                Bukkit.getPluginManager().disablePlugin(plugin)
            }
        }.start()
    }

    fun checkForUpdatesAsync() {
        val local: String = plugin.description.version
        val thread = Thread {
            val request = HttpRequest.newBuilder(URI.create("https://raw.githubusercontent.com/dmzz-yyhyy/PotatoIpDisplay/bukkit/PLUGIN_VERSION")).GET().build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                val remote = response.body().trim().split("=")[1]
                when (compareVersions(local, remote)) {
                    -1 -> plugin.log("Update available: $remote") /* local < remote */
                    0 -> plugin.log("Up to date.") /* local = remote */
                    1 -> return@Thread /* local > remote, NEWER than remote??? */
                    else -> plugin.log("error while comparing versions: $local vs $remote")
                }
            }
        }
        thread.start()
    }

    /* p=version[P]lugin, r=version[R]emote */
    private fun compareVersions(p: String, r: String): Int {
        val px = p.split(".")
        val rx = r.split(".")
        for (i in 0..<min(px.size, rx.size)) {
            val n1 = px[i].toInt()
            val n2 = rx[i].toInt()
            if (n1 != n2) {
                return n1.compareTo(n2)
            }
        }
        return px.size.compareTo(rx.size)
    }



}