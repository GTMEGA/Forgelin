package net.shadowfacts.forgelin.preloader;

import lombok.val;
import net.shadowfacts.forgelin.Tags;

import cpw.mods.fml.relauncher.IFMLCallHook;

import java.util.Map;

public final class ForgelinSetup implements IFMLCallHook {
    @Override
    public void injectData(Map<String, Object> data) {
        if (data == null)
            return;
        val loader = (ClassLoader) data.get("classLoader");
        if (loader == null)
            return;
        try {
            loader.loadClass(Tags.ROOT_PKG + ".KotlinAdapter");
        } catch (ClassNotFoundException e) {
            // this should never happen
            throw new RuntimeException("Couldn't find Forgelin language adapter, this shouldn't be happening", e);
        }
    }

    @Override
    public Void call() throws Exception {
        return null;
    }
}
