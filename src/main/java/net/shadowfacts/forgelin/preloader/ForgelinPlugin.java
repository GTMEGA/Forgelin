package net.shadowfacts.forgelin.preloader;

import net.shadowfacts.forgelin.Tags;
import net.shadowfacts.forgelin.coroutines.MinecraftClientDispatcher;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

//Java class because Kotlin is unsafe to use with IFMLLoadingPlugin (fplib deploader)
public final class ForgelinPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                Tags.ROOT_PKG + ".internal.asm.ASMTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return "net.shadowfacts.forgelin.preloader.ForgelinSetup";
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (FMLLaunchHandler.side().isClient()) {
            //Kotlin call is safe here, deploader has run.
            MinecraftClientDispatcher.setup();
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
