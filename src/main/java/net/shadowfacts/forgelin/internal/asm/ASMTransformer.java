package net.shadowfacts.forgelin.internal.asm;

import com.falsepattern.lib.turboasm.MergeableTurboTransformer;
import com.falsepattern.lib.turboasm.TurboClassTransformer;
import lombok.var;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Referenced by {@link net.shadowfacts.forgelin.preloader.ForgelinPlugin}
 */
@SuppressWarnings("UnstableApiUsage")
public class ASMTransformer extends MergeableTurboTransformer {
    public ASMTransformer() {
        super(getTransformers());
    }

    private static List<TurboClassTransformer> getTransformers() {
        var res = new ArrayList<TurboClassTransformer>();
        res.add(new MinecraftServerThreadUtilTransformer());
        if (FMLLaunchHandler.side().isClient()) {
            res.add(new MinecraftClientThreadUtilTransformer());
        }
        return res;
    }
}
