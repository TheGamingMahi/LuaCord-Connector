package io.luacord.connector

import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

/**
 * JAR connector for LuaCord plugins
 *
 * This class acts as a bridge between a standard Bukkit plugin JAR
 * and an embedded LuaCord .lkt plugin file.
 *
 * @author TheGamingMahi
 */
class PluginConnector : JavaPlugin() {

    companion object {
        private const val REQUIRED_LUACORD_VERSION = "0.2.0"
        private const val LKT_RESOURCE_PATH = "plugin.lkt"

        // Download URLs
        private const val CURSEFORGE_URL = "https://curseforge.com/minecraft/bukkit-plugins/luacord"
        private const val MODRINTH_URL = "https://modrinth.com/plugin/luacord"
    }

    override fun onEnable() {
        logger.info("${ChatColor.AQUA}Initializing LuaCord plugin connector...")

        // Step 1: Check if LuaCord is installed
        val luacord = server.pluginManager.getPlugin("LuaCord")

        if (luacord == null) {
            showMissingLuaCordError()
            server.pluginManager.disablePlugin(this)
            return
        }

        // Step 2: Check LuaCord version compatibility
        val luacordVersion = luacord.description.version
        logger.info("Found LuaCord version: $luacordVersion")

        if (!isCompatibleVersion(luacordVersion)) {
            logger.warning("${ChatColor.YELLOW}Warning: This plugin requires LuaCord $REQUIRED_LUACORD_VERSION or higher")
            logger.warning("${ChatColor.YELLOW}You have: $luacordVersion")
            logger.warning("${ChatColor.YELLOW}The plugin may not work correctly!")
        }

        // Step 3: Load the embedded .lkt plugin via LuaCord API
        try {
            logger.info("Loading embedded Lua plugin...")

            // Use reflection to call LuaCordAPI.loadLuaPlugin()
            val apiClass = Class.forName("io.thegamingmahi.luacord.LuaCordAPI")
            val loadMethod = apiClass.getMethod(
                "loadLuaPlugin",
                JavaPlugin::class.java,
                String::class.java
            )

            val result = loadMethod.invoke(null, this, LKT_RESOURCE_PATH)

            if (result == null) {
                logger.severe("${ChatColor.RED}Failed to load Lua plugin from JAR!")
                logger.severe("${ChatColor.RED}The embedded .lkt file may be corrupted or missing.")
                server.pluginManager.disablePlugin(this)
                return
            }

            logger.info("${ChatColor.GREEN}✓ Successfully loaded Lua plugin!")

        } catch (e: ClassNotFoundException) {
            logger.severe("${ChatColor.RED}LuaCord API not found!")
            logger.severe("${ChatColor.RED}Your LuaCord version is too old or incompatible.")
            logger.severe("${ChatColor.RED}Please update to LuaCord $REQUIRED_LUACORD_VERSION or higher.")
            showDownloadLinks()
            server.pluginManager.disablePlugin(this)

        } catch (e: NoSuchMethodException) {
            logger.severe("${ChatColor.RED}LuaCord API method not found!")
            logger.severe("${ChatColor.RED}Your LuaCord version is incompatible.")
            logger.severe("${ChatColor.RED}Please update to LuaCord $REQUIRED_LUACORD_VERSION or higher.")
            showDownloadLinks()
            server.pluginManager.disablePlugin(this)

        } catch (e: Exception) {
            logger.severe("${ChatColor.RED}Error loading Lua plugin: ${e.message}")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {
        // Cleanup is handled by LuaCord
        logger.info("${ChatColor.GRAY}Plugin connector disabled")
    }

    /**
     * Check if the installed LuaCord version is compatible
     */
    private fun isCompatibleVersion(version: String): Boolean {
        try {
            // Remove any suffixes like -BETA, -SNAPSHOT
            val cleanVersion = version.split("-")[0]
            val parts = cleanVersion.split(".")

            if (parts.size < 2) return false

            val major = parts[0].toIntOrNull() ?: return false
            val minor = parts[1].toIntOrNull() ?: return false

            val requiredParts = REQUIRED_LUACORD_VERSION.split(".")
            val requiredMajor = requiredParts[0].toInt()
            val requiredMinor = requiredParts[1].toInt()

            // Check if version >= required version
            return when {
                major > requiredMajor -> true
                major == requiredMajor && minor >= requiredMinor -> true
                else -> false
            }
        } catch (e: Exception) {
            logger.warning("Could not parse version: $version")
            return false
        }
    }

    /**
     * Show error message when LuaCord is not installed
     */
    private fun showMissingLuaCordError() {
        logger.severe("========================================")
        logger.severe("${ChatColor.RED}${ChatColor.BOLD}LuaCord is REQUIRED!")
        logger.severe("")
        logger.severe("${ChatColor.YELLOW}This plugin is a LuaCord plugin and needs")
        logger.severe("${ChatColor.YELLOW}LuaCord to be installed on your server.")
        logger.severe("")
        showDownloadLinks()
        logger.severe("========================================")

        // Notify online operators
        server.scheduler.runTaskLater(this, Runnable {
            server.onlinePlayers
                .filter { it.isOp }
                .forEach { player ->
                    player.sendMessage("")
                    player.sendMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[${description.name}]")
                    player.sendMessage("${ChatColor.RED}This plugin requires LuaCord!")
                    player.sendMessage("")
                    player.sendMessage("${ChatColor.YELLOW}Download LuaCord from:")
                    player.sendMessage("${ChatColor.AQUA}CurseForge: $CURSEFORGE_URL")
                    player.sendMessage("${ChatColor.AQUA}Modrinth: $MODRINTH_URL")
                    player.sendMessage("")
                }
        }, 20L) // 1 second delay
    }

    /**
     * Show download links
     */
    private fun showDownloadLinks() {
        logger.severe("${ChatColor.YELLOW}Download LuaCord from:")
        logger.severe("${ChatColor.AQUA}• CurseForge: $CURSEFORGE_URL")
        logger.severe("${ChatColor.AQUA}• Modrinth: $MODRINTH_URL")
    }
}