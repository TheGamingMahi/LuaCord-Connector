# LuaCord Connector

JAR connector template for wrapping LuaCord plugins for distribution on CurseForge and Modrinth.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## What is This?

This project builds a template JAR that acts as a bridge between standard Bukkit plugin JARs and LuaCord `.lkt` plugins.

When users want to distribute their Lua plugins on CurseForge/Modrinth (which only accept `.jar` files), they use the [LuaCord JAR Generator](https://luacordmc.github.io/generator.html) to wrap their `.lkt` file inside this connector template.

---

## How It Works

### Template JAR Structure:
```
luacord-connector-template.jar
├── META-INF/MANIFEST.MF
├── io/luacord/connector/PluginConnector.class
├── kotlin/ (stdlib)
└── plugin.yml (with placeholders)
```

### After JAR Generator Processes It:
```
UserPlugin.jar
├── META-INF/MANIFEST.MF
├── io/luacord/connector/PluginConnector.class
├── kotlin/ (stdlib)
├── plugin.yml (user's metadata)
└── plugin.lkt (user's Lua plugin)
```

---

## What the Connector Does

The `PluginConnector` class:

1. ✅ Checks if LuaCord is installed on the server
2. ✅ Verifies LuaCord version compatibility (0.2.0+)
3. ✅ Shows helpful error messages with download links if LuaCord is missing
4. ✅ Notifies server operators in-game if LuaCord is missing
5. ✅ Calls `LuaCordAPI.loadLuaPlugin()` to load the embedded `.lkt` file
6. ✅ Handles all errors gracefully

---

## plugin.yml Placeholders

The JAR generator replaces these placeholders in `plugin.yml`:

- `PluginNamePlaceholder` → User's plugin name
- `VersionPlaceholder` → User's plugin version
- `AuthorPlaceholder` → User's name
- `DescriptionPlaceholder` → Plugin description

The `main` class is hardcoded: `io.luacord.connector.PluginConnector`

---

## For Plugin Developers

**Don't build this manually!** Instead:

1. Create your `.lkt` plugin
2. Go to [luacordmc.github.io/generator.html](https://luacordmc.github.io/generator.html)
3. Upload your `.lkt` and fill in metadata
4. Download your ready-to-use `.jar`
5. Upload to CurseForge/Modrinth!

The JAR generator uses this template automatically.

---

## Requirements

For generated JARs to work on servers:
- LuaCord 0.2.0+ installed on the server
- Spigot or Paper server
- Java 8+

---

## Error Messages

When LuaCord is not installed, the connector shows:

**In Console:**
```
[PluginName] ========================================
[PluginName] LuaCord is REQUIRED!
[PluginName] 
[PluginName] This plugin is a LuaCord plugin and needs
[PluginName] LuaCord to be installed on your server.
[PluginName] 
[PluginName] Download LuaCord from:
[PluginName] • CurseForge: https://curseforge.com/...
[PluginName] • Modrinth: https://modrinth.com/...
[PluginName] ========================================
```

**In-Game (to operators):**
```
[PluginName]
This plugin requires LuaCord!

Download LuaCord from:
CurseForge: https://curseforge.com/...
Modrinth: https://modrinth.com/...
```

---

## Technical Details

### How It Loads Plugins:

The connector uses reflection to call the LuaCord API:

```kotlin
val apiClass = Class.forName("io.thegamingmahi.luacord.LuaCordAPI")
val loadMethod = apiClass.getMethod(
    "loadLuaPlugin",
    JavaPlugin::class.java,
    String::class.java
)

val result = loadMethod.invoke(null, this, "plugin.lkt")
```

This approach:
- ✅ Doesn't require LuaCord at compile time
- ✅ Shows helpful errors if LuaCord is missing
- ✅ Works with any LuaCord 0.2.0+ version

---

## License

**MIT License** - Maximum freedom for plugin developers.

This allows plugin developers to:
- ✅ Use in commercial plugins
- ✅ Distribute closed-source
- ✅ Modify as needed
- ✅ No attribution required (but appreciated!)

The LuaCord core is GPL v3, but this connector template is MIT to give plugin developers full freedom.

---

## Links

- **LuaCord Main Project:** https://github.com/TheGamingMahi/LuaCord
- **JAR Generator:** https://luacordmc.github.io/generator.html
- **LuaCord Website:** https://luacordmc.github.io
- **Issues:** https://github.com/TheGamingMahi/LuaCord/issues

---

## Credits

**Created by TheGamingMahi** for the LuaCord project.

Part of the LuaCord ecosystem - making Lua plugin development and distribution easy!

---

**Questions?** Open an issue on the [LuaCord repository](https://github.com/TheGamingMahi/LuaCord/issues)!
