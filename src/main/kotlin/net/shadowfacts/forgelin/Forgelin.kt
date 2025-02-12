package net.shadowfacts.forgelin

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent

/**
 * @author shadowfacts
 */
@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.MOD_VERSION,
    acceptableRemoteVersions = "*",
    acceptedMinecraftVersions = "*",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object Forgelin {

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        Loader.instance().modList.forEach {
            ForgelinAutomaticEventSubscriber.subscribeAutomatic(it, event.asmData, FMLCommonHandler.instance().side)
        }
    }
}
